package io.github.seed.module.sms.sender;

import io.github.seed.module.sms.data.SmsMessage;
import io.github.seed.module.sms.data.SmsResult;

import java.util.Map;

/**
 * 短信发送器
 * <br>对接短信服务商时只需实现本接口并注册为Bean，默认的
 * {@link io.github.seed.module.sms.sender.LogSmsSender}会自动让位；
 * 推荐继承{@link AbstractSmsSender}，可复用签名兜底、日志与异常处理，只需实现真正调用SDK的那一步
 * <br>业务方一般直接使用{@link #sendText(String, String)}与
 * {@link #sendTemplate(String, String, Map)}，无需自己组装{@link SmsMessage}
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SmsSender {

    /**
     * 发送短信
     * <br>实现方应保证本方法不抛出异常，发送失败时返回
     * {@link SmsResult#isSuccess()}为false的结果，由调用方决定如何处理
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
     * @param templateCode   模板编码
     * @param templateParams 模板参数
     * @return 发送结果
     */
    default SmsResult sendTemplate(String mobile, String templateCode, Map<String, Object> templateParams) {
        return this.send(SmsMessage.template(mobile, templateCode, templateParams));
    }

}
