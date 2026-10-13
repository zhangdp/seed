package io.github.seed.manager;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.module.captcha.CaptchaProperties;
import io.github.seed.module.captcha.CaptchaService;
import io.github.seed.module.captcha.data.CaptchaScene;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.module.sms.data.SmsMessage;
import io.github.seed.module.sms.limiter.SmsSendLimiter;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 验证码编排层：把「生成验证码」与「投递」串起来——{@link CaptchaService}只管生成、缓存与频率控制，
 * {@link SmsManager}只管短信落库与发送，因此模块层不必知道另外两方的存在
 * <br>短信为「落库排队 -> 调度发送」两段式，本方法只新增不发送、不阻塞调用方
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaManager {

    private final CaptchaService captchaService;
    private final CaptchaProperties captchaProperties;
    private final SmsProperties smsProperties;
    private final SmsManager smsManager;
    private final SmsSendLimiter smsSendLimiter;

    /**
     * 生成短信验证码并投递：场景在{@code app.sms.templates}配了模板时走模板短信，
     * 否则按{@code app.captcha.sms-content-template}发文本短信
     * <br>限流放在生成验证码之后、落库之前：限的是「把短信发出去」而不是「生成码」，
     * 因此场景白名单这类验证码自己的校验不受影响
     *
     * @param scene  场景，如login、reset_password，为空使用默认场景
     * @param mobile 手机号
     */
    public void sendSms(String scene, String mobile) {
        // 场景先规范化，保证验证码缓存的场景与短信记录的场景是同一个
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        String code = captchaService.generateSmsCode(s, mobile);
        // 抢占式占用发送锁，避免"检查-发送"竞态导致并发短信轰炸；抢占失败则本次生成的码作废
        if (!smsSendLimiter.tryLock(s, mobile, smsProperties.getSendInterval())) {
            Duration remain = smsSendLimiter.getLockExpire(s, mobile);
            throw new BizException(ErrorCode.CAPTCHA_SEND_TOO_FREQUENTLY.code(),
                    "请" + Math.max(remain.toSeconds(), 1) + "秒后再试");
        }
        smsManager.create(this.buildSmsMessage(s, mobile, code), s);
    }

    /**
     * 渲染验证码短信：场景配置了模板则走模板短信，否则退化为文本短信
     *
     * @param scene  场景
     * @param mobile 手机号
     * @param code   验证码
     * @return 短信内容
     */
    private SmsMessage buildSmsMessage(String scene, String mobile, String code) {
        String templateCode = smsProperties.getTemplate(scene);
        if (StrUtil.isBlank(templateCode)) {
            long minutes = Math.max(captchaProperties.getSmsExpire().toMinutes(), 1);
            return SmsMessage.text(mobile, StrUtil.format(captchaProperties.getSmsContentTemplate(), code, minutes));
        }
        // 模板参数用有序Map，便于对接腾讯云这类以参数顺序对应模板占位符的服务商
        Map<String, Object> templateParams = new LinkedHashMap<>();
        templateParams.put(captchaProperties.getSmsCodeParam(), code);
        return SmsMessage.template(mobile, templateCode, templateParams);
    }

}
