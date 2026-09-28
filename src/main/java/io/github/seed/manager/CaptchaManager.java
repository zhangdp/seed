package io.github.seed.manager;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.module.captcha.CaptchaProperties;
import io.github.seed.module.captcha.CaptchaService;
import io.github.seed.module.captcha.data.CaptchaScene;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.module.sms.data.SmsMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 验证码管理器
 * <br>编排层：把「生成验证码」与「把验证码送出去」两件事串起来——
 * {@link CaptchaService}只管验证码的生成、缓存与频率控制，{@link SmsManager}只管短信的落库与发送，
 * 本类负责编排二者，因此模块层不必知道编排层与短信模块的存在
 * <br>短信由{@link SmsManager}「新增落库排队 -> 调度发送」两段式处理，本方法只新增不发送、不阻塞调用方
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

    /**
     * 生成短信验证码并投递
     * <br>先由{@link CaptchaService#generateSmsCode}完成频率校验与验证码生成，
     * 再把验证码渲染成短信交给{@link SmsManager}入队，发送由调度方（定时任务/消息队列）完成
     * <br>短信内容：场景在{@code app.sms.templates}中配置了模板时走模板短信，
     * 未配置则按{@code app.captcha.sms-content-template}发送文本短信
     *
     * @param scene  场景，如login、reset_password，为空使用默认场景
     * @param mobile 手机号
     */
    public void sendSms(String scene, String mobile) {
        // 场景先规范化，保证验证码缓存的场景与短信记录的场景是同一个
        String s = StrUtil.defaultIfBlank(scene, CaptchaScene.DEFAULT);
        String code = captchaService.generateSmsCode(s, mobile);
        smsManager.create(this.buildSmsMessage(s, mobile, code), s);
    }

    /**
     * 构造验证码短信：场景配置了模板则走模板短信，否则退化为文本短信
     *
     * @param scene  场景
     * @param mobile 手机号
     * @param code   验证码
     * @return
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
