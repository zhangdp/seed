package io.github.seed.module.sms.sender;

import io.github.seed.module.sms.data.SmsMessage;
import io.github.seed.module.sms.data.SmsResult;

import java.util.Map;

/**
 * 短信发送器：对接服务商时建议继承{@link AbstractSmsSender}并注册为Bean，
 * 默认的{@link LogSmsSender}会自动让位
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SmsSender {

    /**
     * 发送短信，实现方不应抛出异常，失败时返回失败结果
     *
     * @param message 短信内容
     * @return 发送结果
     */
    SmsResult send(SmsMessage message);

    /**
     * 发送文本短信
     *
     * @param mobile  手机号
     * @param content 短信正文
     * @return 发送结果
     */
    default SmsResult sendText(String mobile, String content) {
        return this.send(SmsMessage.text(mobile, content));
    }

    /**
     * 发送模板短信
     *
     * @param mobile         手机号
     * @param templateCode   服务商模板编码
     * @param templateParams 模板参数
     * @return 发送结果
     */
    default SmsResult sendTemplate(String mobile, String templateCode, Map<String, Object> templateParams) {
        return this.send(SmsMessage.template(mobile, templateCode, templateParams));
    }

}
