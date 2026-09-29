package io.github.seed.module.sms.data;

import lombok.Getter;

/**
 * 短信发送结果：各服务商的返回字段名称不同，这里统一成同一组语义
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
public class SmsResult {

    /**
     * 无附加信息的成功结果
     */
    private static final SmsResult SUCCESS = new SmsResult(true, null, null, null, null);

    /**
     * 是否发送成功
     */
    private final boolean success;
    /**
     * 服务商请求id，提交工单排查问题时需要提供
     */
    private final String requestId;
    /**
     * 服务商回执id，用于向服务商查询该条短信的发送详情
     */
    private final String bizId;
    /**
     * 服务商返回码，成功时一般是OK
     */
    private final String code;
    /**
     * 服务商返回描述，失败时为失败原因
     */
    private final String message;

    private SmsResult(boolean success, String requestId, String bizId, String code, String message) {
        this.success = success;
        this.requestId = requestId;
        this.bizId = bizId;
        this.code = code;
        this.message = message;
    }

    /**
     * 发送成功，无服务商信息
     *
     * @return 成功结果
     */
    public static SmsResult ok() {
        return SUCCESS;
    }

    /**
     * 发送成功，携带服务商的请求id与回执id
     *
     * @param requestId 服务商请求id，可为空
     * @param bizId     服务商回执id，可为空
     * @return 成功结果
     */
    public static SmsResult ok(String requestId, String bizId) {
        return new SmsResult(true, requestId, bizId, null, null);
    }

    /**
     * 发送失败
     *
     * @param code    服务商返回码或自定义错误标识，可为空
     * @param message 失败原因
     * @return 失败结果
     */
    public static SmsResult fail(String code, String message) {
        return new SmsResult(false, null, null, code, message);
    }

    /**
     * @return 是否发送失败
     */
    public boolean isFail() {
        return !this.success;
    }

}
