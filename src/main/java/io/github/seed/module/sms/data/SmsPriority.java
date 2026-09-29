package io.github.seed.module.sms.data;

/**
 * 短信优先级：值越大越优先发送，调度方取件时按「优先级降序、入库顺序」排序
 * <br>优先级只影响发送顺序，不影响重试：任何优先级失败后都按{@code app.sms.max-retry}重试
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum SmsPriority {

    /**
     * 普通，默认级别
     */
    NORMAL(0, "普通"),
    /**
     * 重要：如密码找回、交易提醒
     */
    IMPORTANT(1, "重要"),
    /**
     * 紧急：如系统告警、安全风险提示
     */
    URGENT(2, "紧急");

    private final int priority;
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
