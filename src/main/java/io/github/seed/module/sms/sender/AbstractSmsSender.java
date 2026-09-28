package io.github.seed.module.sms.sender;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.module.sms.data.SmsMessage;
import io.github.seed.module.sms.data.SmsResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信发送器抽象基类
 * <br>封装了签名兜底、统一日志与异常兜底，对接服务商时只需继承本类并实现
 * {@link #doSend(SmsMessage)}，把{@link SmsMessage}映射到服务商SDK的请求对象即可
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public abstract class AbstractSmsSender implements SmsSender {

    protected final SmsProperties smsProperties;

    protected AbstractSmsSender(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    /**
     * 发送短信，不允许子类覆盖，保证签名兜底、日志与异常处理对所有实现一致
     * <br>本方法不会抛出异常，发送失败或异常时返回失败结果
     *
     * @param message 短信内容
     * @return 发送结果，不会为null
     */
    @Override
    public final SmsResult send(SmsMessage message) {
        // 单条消息未指定签名时使用全局配置的签名
        if (StrUtil.isBlank(message.getSignName())) {
            message.signName(smsProperties.getSignName());
        }
        long start = System.currentTimeMillis();
        try {
            SmsResult result = this.doSend(message);
            if (result == null) {
                result = SmsResult.fail(null, "短信服务商未返回发送结果");
            }
            long cost = System.currentTimeMillis() - start;
            if (result.isSuccess()) {
                log.info("发送短信成功：mobile={}, signName={}, {}, requestId={}, bizId={}, 耗时={}ms",
                        message.getMobile(), message.getSignName(), this.describe(message),
                        result.getRequestId(), result.getBizId(), cost);
            } else {
                log.warn("发送短信失败：mobile={}, signName={}, {}, code={}, message={}, 耗时={}ms",
                        message.getMobile(), message.getSignName(), this.describe(message),
                        result.getCode(), result.getMessage(), cost);
            }
            return result;
        } catch (Exception e) {
            log.error("发送短信异常：mobile={}, signName={}, {}", message.getMobile(), message.getSignName(),
                    this.describe(message), e);
            return SmsResult.fail(null, e.getMessage());
        }
    }

    /**
     * 真正调用服务商SDK发送短信，允许抛出异常，由基类统一兜底为失败结果
     *
     * @param message 短信内容，签名已由基类兜底填充
     * @return 发送结果
     * @throws Exception 调用服务商SDK失败时抛出
     */
    protected abstract SmsResult doSend(SmsMessage message) throws Exception;

    /**
     * 描述短信内容，用于日志
     *
     * @param message 短信内容
     * @return
     */
    protected String describe(SmsMessage message) {
        return message.isTemplate()
                ? StrUtil.format("templateCode={}, params={}", message.getTemplateCode(), message.getTemplateParams())
                : "content=" + message.getContent();
    }

}
