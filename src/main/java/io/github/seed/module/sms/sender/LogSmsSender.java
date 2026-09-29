package io.github.seed.module.sms.sender;

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
         * todo 对接服务商时：新增一个继承 AbstractSmsSender 的实现并注册为Bean（推荐，本Mock留作本地联调），
         * 或直接改本方法——都只需把 SmsMessage 映射到SDK的请求对象，
         * 再用 SmsResult.ok(requestId, bizId) / SmsResult.fail(code, message) 包装返回
         * 注意：腾讯云的模板参数是有序数组，按 getTemplateParams() 的插入顺序转换；签名必须与报备一致
         */
        log.warn("[短信-Mock] 未接入短信服务商，短信未真实发送：mobile={}, signName={}, {}",
                message.getMobile(), message.getSignName(), this.describe(message));
        return SmsResult.ok("mock-" + System.currentTimeMillis(), null);
    }

}
