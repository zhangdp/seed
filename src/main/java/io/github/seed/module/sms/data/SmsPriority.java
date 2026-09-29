package io.github.seed.module.sms.data;

/**
 * 短信优先级：值越大越紧急，调度方取件时按优先级从高到低、同优先级按入库顺序发送
 * <br>排队积压时高优先级短信先发出，例如系统告警类短信应高于营销类短信
 * <br>优先级只影响发送<b>顺序</b>，不影响重试策略：任何优先级的短信发送失败后都按
 * {@code app.sms.max-retry}重试，退回待发送后仍按自己的优先级参与排队
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum SmsPriority {

    /**
     * 普通：默认级别，如验证码、通知类短信
     */
    NORMAL(0, "普通"),
    /**
     * 重要：需要尽快送达，如密码找回、交易提醒
     */
    IMPORTANT(1, "重要"),
    /**
     * 紧急：优先级最高，如系统告警、安全风险提示
     */
    URGENT(2, "紧急");

    /**
     * 优先级值
     */
    private final int priority;
    /**
     * 优先级描述
     */
    private final String desc;

    SmsPriority(int priority, String desc) {
        this.priority = priority;
        this.desc = desc;
    }

    public int priority() {
        return this.priority;
    }

    public String desc() {
        return this.desc;
    }
}
