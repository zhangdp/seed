package io.github.seed.module.sms.data;

/**
 * 短信发送状态，流程为「新增落库排队 -> 调度抢占 -> 发送 -> 回写状态」
 * <br>{@link #SENDING}由CAS更新产生（见{@code SmsLogMapper#claim}），
 * 因此多节点并发调度时同一记录只会被一个节点领取
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum SmsStatus {

    /**
     * 待发送，排队中；发送失败但未达最大重试次数也会退回该状态
     */
    PENDING(0, "待发送"),
    /**
     * 发送中，已被某节点抢占
     */
    SENDING(1, "发送中"),
    /**
     * 发送成功
     */
    SUCCESS(2, "发送成功"),
    /**
     * 发送失败，终态：已达最大重试次数仍未成功
     */
    FAIL(3, "发送失败");

    private final int status;
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
