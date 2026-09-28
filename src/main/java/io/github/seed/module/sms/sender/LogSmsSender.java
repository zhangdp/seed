package io.github.seed.module.sms.sender;

import io.github.seed.module.sms.sender.AbstractSmsSender;
import io.github.seed.module.sms.data.SmsMessage;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.module.sms.data.SmsResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信发送的日志Mock实现，只打印日志不会真实发送短信
 * <br>用于本地联调，也是接入短信服务商前的默认实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class LogSmsSender extends AbstractSmsSender {

    public LogSmsSender(SmsProperties smsProperties) {
        super(smsProperties);
    }

    @Override
    protected SmsResult doSend(SmsMessage message) {
        /*
         * todo 对接云服务商后替换本实现，两种接入姿势：
         *  a、新增一个继承 AbstractSmsSender 的 @Component 或 @Bean，只实现 doSend，
         *     SmsConfigurer 中的 @ConditionalOnMissingBean 会自动让位给自定义实现（推荐，本Mock可继续用于本地联调）
         *  b、直接改本类的方法体
         *
         * 常见服务商的适配要点（把 SmsMessage 映射到各自的请求对象即可）：
         *  1、阿里云 dysmsapi20170525：SendSmsRequest.setPhoneNumbers/setSignName/setTemplateCode/
         *     setTemplateParam(JSON字符串)，可直接把 getTemplateParams() 序列化为JSON；
         *     纯文本内容需单独报备，未报备时只能按模板发送
         *  2、腾讯云 tencentcloud-sdk-java-sms：SendSmsRequest.setPhoneNumberSet/setSignName/setTemplateId/
         *     setTemplateParamSet，模板参数是<b>有序</b>数组，按 getTemplateParams() 的插入顺序转成数组
         *  3、其他服务商（云片、容联、Twilio等）：同样只需映射请求对象，返回时用
         *     SmsResult.ok(requestId, bizId) 或 SmsResult.fail(code, message) 包装
         *
         * 注意：SignName 必须使用与服务商报备一致的签名，否则会被服务商拒绝（返回签名不合法）
         */
        log.warn("[短信-Mock] 未接入短信服务商，短信未真实发送：mobile={}, signName={}, {}",
                message.getMobile(), message.getSignName(), this.describe(message));
        return SmsResult.ok("mock-" + System.currentTimeMillis(), null);
    }

}
