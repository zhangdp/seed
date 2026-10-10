package io.github.seed.module.job.data;

/**
 * 定时任务错过触发时间后的补偿策略
 * <br>错过常见于：节点全停了一段时间、上一次执行耗时超过了触发间隔
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum JobMisfirePolicy {

    /**
     * 立即补跑一次，然后按cron继续
     */
    FIRE_NOW("FIRE_NOW"),
    /**
     * 放弃这次，直接按cron推算下一次，避免停了很久之后集中补跑压垮下游
     */
    SKIP("SKIP");

    /**
     * 存储值，显式写死而非取{@link #name()}——将来改枚举常量名不会连带改数据
     */
    private final String value;

    JobMisfirePolicy(String value) {
        this.value = value;
    }

    /**
     * 获取存储值
     *
     * @return 存储值
     */
    public String value() {
        return this.value;
    }

    /**
     * 按存储值取枚举，取不到时返回{@link #SKIP}
     *
     * @param value 存储值
     * @return 对应的枚举
     */
    public static JobMisfirePolicy of(String value) {
        if (value == null) {
            return SKIP;
        }
        for (JobMisfirePolicy policy : values()) {
            if (policy.value.equals(value)) {
                return policy;
            }
        }
        return SKIP;
    }
}
