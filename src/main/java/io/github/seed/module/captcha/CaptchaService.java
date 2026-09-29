package io.github.seed.module.captcha;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.module.captcha.data.CaptchaScene;
import io.github.seed.module.captcha.data.CaptchaType;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.model.dto.ImageCaptcha;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

/**
 * 验证码服务：只管验证码的生命周期（生成、缓存、校验、发送频率），<b>不负责投递</b>
 * <br>投递由编排层{@code CaptchaManager}决定，模块层因此不必知道短信模块的存在，换邮件也不影响本类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaService {

    private final CaptchaProperties captchaProperties;
    private final CaptchaStore captchaStore;
    private final CaptchaGenerator captchaGenerator;

    /**
     * 生成图片验证码；每次生成都由服务端生成唯一key并随图片返回，因此多处验证码不会串号
     *
     * @param scene  场景，为空使用默认场景
     * @param width  图片宽高，为空或非法时用配置的默认值，并夹取到配置范围内
     * @param height 同上
     * @return 图片验证码
     */
    public ImageCaptcha generateImage(String scene, Integer width, Integer height) {
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        int w = this.clamp(width, captchaProperties.getImageWidth(), captchaProperties.getImageMinWidth(),
                captchaProperties.getImageMaxWidth());
        int h = this.clamp(height, captchaProperties.getImageHeight(), captchaProperties.getImageMinHeight(),
                captchaProperties.getImageMaxHeight());
        CaptchaGenerator.GeneratedImage generated = captchaGenerator.generateImage(w, h,
                captchaProperties.getImageCodeLength(), captchaProperties.getImageLineCount());
        String key = captchaGenerator.generateKey();
        captchaStore.saveCode(CaptchaType.IMAGE, s, key, generated.code(), captchaProperties.getImageExpire());
        ImageCaptcha imageCaptcha = new ImageCaptcha();
        imageCaptcha.setKey(key);
        imageCaptcha.setImage(generated.image());
        return imageCaptcha;
    }

    /**
     * 生成短信验证码：校验发送资格后生成并缓存，<b>不涉及任何投递动作</b>，返回值交给编排层决定怎么送达
     * <br>发送锁是接口层的频率限制，按时间到期自动释放，与短信最终是否送达无关——
     * 否则服务商故障时锁会失效，且服务商偶有「返回失败但实际已送达」的情况，此时作废验证码
     * 反而会让用户手里那条真收到的码失效
     *
     * @param scene  场景，如login、reset_password，为空使用默认场景
     * @param mobile 手机号
     * @return 本次生成的验证码
     */
    public String generateSmsCode(String scene, String mobile) {
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        // 1、场景白名单校验，防止前端轮换场景绕过发送频率限制
        Set<String> scenes = captchaProperties.getSmsScenes();
        if (scenes != null && !scenes.isEmpty() && !scenes.contains(s)) {
            throw new BizException(ErrorCode.CAPTCHA_SCENE_INVALID.code(),
                    ErrorCode.CAPTCHA_SCENE_INVALID.message() + "：" + s);
        }
        // 2、抢占式写入发送锁，避免"检查-发送"竞态导致并发短信轰炸
        //    先于当日计数占用，避免用户频繁重试把当日额度白白耗光
        if (!captchaStore.tryLockSend(CaptchaType.SMS, s, mobile, captchaProperties.getSmsSendInterval())) {
            Duration remain = captchaStore.getExpire(captchaStore.limitKey(CaptchaType.SMS, s, mobile));
            throw new BizException(ErrorCode.CAPTCHA_SEND_TOO_FREQUENTLY.code(),
                    "请" + Math.max(remain.toSeconds(), 1) + "秒后再试");
        }
        // 3、手机号当日发送上限
        int maxPerDay = captchaProperties.getSmsMaxPerMobilePerDay();
        if (maxPerDay > 0 && captchaStore.increaseSmsDaily(mobile) > maxPerDay) {
            captchaStore.releaseSendLock(CaptchaType.SMS, s, mobile);
            throw new BizException(ErrorCode.CAPTCHA_SEND_TOO_FREQUENTLY.code(),
                    "该手机号今日验证码发送次数已达上限");
        }
        // 4、生成并缓存验证码
        String code = captchaGenerator.generateSmsCode(captchaProperties.getSmsCodeLength());
        captchaStore.saveCode(CaptchaType.SMS, s, mobile, code, captchaProperties.getSmsExpire());
        return code;
    }

    /**
     * 校验验证码，失败或验证码不存在时抛出{@link BizException}
     *
     * @param type            验证码类型
     * @param scene           场景
     * @param key             验证码key
     * @param code            待校验的验证码
     * @param failCount       允许失败次数，达到即作废验证码
     * @param removeOnSuccess 校验成功后是否删除验证码
     * @param message         自定义错误提示，为空使用默认提示
     */
    public void verify(CaptchaType type, String scene, String key, String code,
                       int failCount, boolean removeOnSuccess, String message) {
        String cacheCode = captchaStore.getCode(type, scene, key);
        if (StrUtil.isBlank(cacheCode)) {
            throw new BizException(ErrorCode.CAPTCHA_EXPIRED.code(),
                    StrUtil.defaultIfBlank(message, ErrorCode.CAPTCHA_EXPIRED.message()));
        }
        if (!StrUtil.equalsIgnoreCase(cacheCode, code)) {
            this.handleVerifyFail(type, scene, key, failCount);
            throw new BizException(ErrorCode.CAPTCHA_INCORRECT.code(),
                    StrUtil.defaultIfBlank(message, ErrorCode.CAPTCHA_INCORRECT.message()));
        }
        // 校验通过，清掉失败计数；验证码默认按一次性消费处理
        captchaStore.deleteFail(type, scene, key);
        if (removeOnSuccess) {
            captchaStore.deleteCode(type, scene, key);
        }
    }

    /**
     * 处理校验失败：累计失败次数，达到上限后作废验证码
     *
     * @param type         验证码类型
     * @param scene        场景
     * @param key          验证码key
     * @param maxFailCount 允许失败次数，达到即作废验证码
     */
    private void handleVerifyFail(CaptchaType type, String scene, String key, int maxFailCount) {
        if (maxFailCount <= 1) {
            // 默认策略：失败一次即作废，两条DEL即可完成
            captchaStore.deleteCode(type, scene, key);
            captchaStore.deleteFail(type, scene, key);
            return;
        }
        Duration ttl = captchaStore.getExpire(captchaStore.codeKey(type, scene, key));
        if (captchaStore.increaseFail(type, scene, key, ttl) >= maxFailCount) {
            captchaStore.deleteCode(type, scene, key);
            captchaStore.deleteFail(type, scene, key);
        }
    }

    /**
     * 把值夹取到指定范围内，值非法时使用默认值
     *
     * @param value        待夹取的值
     * @param defaultValue 默认值
     * @param min          最小值
     * @param max          最大值
     * @return 夹取后的值
     */
    private int clamp(Integer value, int defaultValue, int min, int max) {
        int v = value == null || value <= 0 ? defaultValue : value;
        return Math.min(Math.max(v, min), max);
    }

}
