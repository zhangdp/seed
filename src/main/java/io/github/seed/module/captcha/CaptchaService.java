package io.github.seed.module.captcha;

import cn.hutool.v7.core.text.StrUtil;
import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.module.captcha.component.CaptchaGenerator;
import io.github.seed.module.captcha.data.CaptchaScene;
import io.github.seed.module.captcha.data.CaptchaTicket;
import io.github.seed.module.captcha.data.CaptchaType;
import io.github.seed.module.captcha.store.CaptchaStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * 验证码服务，模块对外的唯一入口：行为验证码（滑块/旋转/拼接/文字点选）的生成与校验、短信验证码的生成
 * <br>行为验证码统一出入口：generateImage生成、checkImage校验轨迹并签发一次性票据，
 * 不同类型对调用方完全透明；短信码只生成不投递，交给{@code CaptchaManager}编排
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class CaptchaService {

    /**
     * 一次性票据码位数
     */
    private static final int TICKET_CODE_LENGTH = 6;

    private final CaptchaProperties captchaProperties;
    private final CaptchaStore captchaStore;
    private final CaptchaGenerator captchaGenerator;
    private final ImageCaptchaApplication imageCaptchaApplication;
    /**
     * 类型随机用，与SecureRandom相比这里只影响选图，Random足够
     */
    private final SecureRandom random = new SecureRandom();

    /**
     * 生成行为验证码，不传类型时从启用的类型中随机；返回的图片与类型交给前端渲染对应的交互组件
     *
     * @param type 验证码类型（SLIDER/ROTATE/CONCAT/WORD_IMAGE_CLICK），为空随机
     * @return 验证码数据（id、类型、背景图与模板图base64、尺寸）
     */
    public ImageCaptchaVO generateImage(String type) {
        Set<String> types = captchaProperties.getImage().getTypes();
        if (StrUtil.isBlank(type)) {
            List<String> candidates = List.copyOf(types);
            type = candidates.get(random.nextInt(candidates.size()));
        } else if (!types.contains(type)) {
            throw new BizException(ErrorCode.CAPTCHA_TYPE_INVALID.code(),
                    ErrorCode.CAPTCHA_TYPE_INVALID.message() + "：" + type);
        }
        ApiResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(type);
        if (!response.isSuccess() || response.getData() == null) {
            log.error("行为验证码生成失败：type={}, code={}, msg={}", type, response.getCode(), response.getMsg());
            throw new BizException(ErrorCode.CAPTCHA_SEND_FAILED.code(), "验证码生成失败，请稍后再试");
        }
        return response.getData();
    }

    /**
     * 校验行为验证码轨迹：校验是一次性消费——无论成败，服务端的校验数据即被移除，
     * 失败必须重新获取验证码；成功后签发一次性票据，业务接口凭票据的key与code走
     * {@code @VerifyCaptcha}切面校验
     *
     * @param scene 场景，必须与业务接口{@code @VerifyCaptcha(scene=...)}一致，为空使用默认场景
     * @param id    验证码id
     * @param track 用户操作轨迹
     * @return 一次性票据
     */
    public CaptchaTicket checkImage(String scene, String id, ImageCaptchaTrack track) {
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        ApiResponse<?> response = imageCaptchaApplication.matching(id, track);
        if (!response.isSuccess()) {
            throw new BizException(ErrorCode.CAPTCHA_INCORRECT.code(),
                    StrUtil.defaultIfBlank(response.getMsg(), ErrorCode.CAPTCHA_INCORRECT.message()));
        }
        // 票据存进通用验证码存储，复用@VerifyCaptcha切面的既有校验与消费逻辑
        String key = captchaGenerator.generateKey();
        String code = captchaGenerator.generateCode(TICKET_CODE_LENGTH);
        Duration expire = captchaProperties.getImage().getTicketExpire();
        captchaStore.saveCode(CaptchaType.IMAGE, s, key, code, expire);
        return new CaptchaTicket(key, code);
    }

    /**
     * 生成短信验证码：校验场景后生成并缓存，<b>不涉及任何投递动作</b>
     * <br>发送频率与配额由{@code sms}模块在投递前把关，本方法只保证验证码本身可用
     *
     * @param scene  场景，如login、reset_password，为空使用默认场景
     * @param mobile 手机号
     * @return 本次生成的验证码
     */
    public String generateSmsCode(String scene, String mobile) {
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        // 场景白名单校验，防止前端轮换场景绕过发送频率限制
        Set<String> scenes = captchaProperties.getSms().getScenes();
        if (scenes != null && !scenes.isEmpty() && !scenes.contains(s)) {
            throw new BizException(ErrorCode.CAPTCHA_SCENE_INVALID.code(),
                    ErrorCode.CAPTCHA_SCENE_INVALID.message() + "：" + s);
        }
        CaptchaProperties.Sms sms = captchaProperties.getSms();
        String code = captchaGenerator.generateCode(sms.getCodeLength());
        captchaStore.saveCode(CaptchaType.SMS, s, mobile, code, sms.getExpire());
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
        Duration ttl = captchaStore.getCodeExpire(type, scene, key);
        if (captchaStore.increaseFail(type, scene, key, ttl) >= maxFailCount) {
            captchaStore.deleteCode(type, scene, key);
            captchaStore.deleteFail(type, scene, key);
        }
    }

}
