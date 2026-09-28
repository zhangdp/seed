package io.github.seed.module.sms.data;

/**
 * 短信发送状态
 * <br>短信采用「新增落库排队 -> 调度抢占 -> 发送 -> 回写状态」流程：
 * <ul>
 *     <li>{@link #PENDING}：新增后即处于该状态，等待调度方领取；发送失败但未达最大重试次数也会退回该状态</li>
 *     <li>{@link #SENDING}：已被某节点抢占、正在发送中。抢占通过CAS更新实现（见{@code SmsLogMapper#claim}），
 *     因此多节点并发调度时同一记录只会被一个节点领取</li>
 *     <li>{@link #SUCCESS}/{@link #FAIL}：终态，分别为发送成功、超过最大重试次数仍失败</li>
 * </ul>
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum SmsStatus {

    /**
     * 待发送：记录已入库排队，等待调度方领取
     */
    PENDING(0, "待发送"),
    /**
     * 发送中：已被调度方抢占，正在调用服务商
     */
    SENDING(1, "发送中"),
    /**
     * 发送成功
     */
    SUCCESS(2, "发送成功"),
    /**
     * 发送失败：已达到最大重试次数仍未成功，终态不再被调度方领取
     */
    FAIL(3, "发送失败");

    /**
     * 状态值
     */
    private final int status;
    /**
     * 状态描述
     */
    private final String desc;

    SmsStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int status() {
        return this.status;
    }

    public String desc() {
        return this.desc;
    }
}
