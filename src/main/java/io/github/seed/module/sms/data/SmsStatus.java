package io.github.seed.module.sms.data;

/**
 * 短信发送状态，流程为「新增落库排队 -> 调度抢占 -> 发送 -> 回写状态」
 * <br>存储值即枚举名，查库时 {@code where status = 'FAIL'} 自解释，不用翻字典
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum SmsStatus {

    /**
     * 待发送，排队中；发送失败但未达最大重试次数也会退回该状态
     */
    PENDING("PENDING", "待发送"),
    /**
     * 发送中，已被某节点抢占
     */
    SENDING("SENDING", "发送中"),
    /**
     * 发送成功
     */
    SUCCESS("SUCCESS", "发送成功"),
    /**
     * 发送失败，终态：已达最大重试次数仍未成功
     */
    FAIL("FAIL", "发送失败");

    /**
     * 存储值，显式写死而非取{@link #name()}——将来改枚举常量名不会连带改数据
     */
    private final String status;
    private final String desc;

    SmsStatus(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    /**
     * 获取存储值
     *
     * @return 状态值
     */
    public String status() {
        return this.status;
    }

    /**
     * 获取状态描述
     *
     * @return 状态描述
     */
    public String desc() {
        return this.desc;
    }
}
