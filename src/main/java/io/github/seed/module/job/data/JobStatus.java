package io.github.seed.module.job.data;

/**
 * 定时任务状态
 * <br>对应quartz里trigger的状态：已停止=PAUSED、待触发=WAITING、执行中=ACQUIRED
 * <br>存储值即枚举名，查库时 {@code where status = 'FIRING'} 自解释，不用翻字典
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum JobStatus {

    /**
     * 已停止，调度器不挑它
     */
    STOPPED("STOPPED"),
    /**
     * 待触发，到了next_fire_time就会被抢占执行
     */
    WAITING("WAITING"),
    /**
     * 执行中，已被某个节点抢占；任务跑完或超时回收后回到其他状态
     */
    FIRING("FIRING");

    /**
     * 存储值，显式写死而非取{@link #name()}——将来改枚举常量名不会连带改数据
     */
    private final String value;

    JobStatus(String value) {
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
     * 按存储值取枚举，取不到时返回null
     *
     * @param value 存储值
     * @return 对应的枚举
     */
    public static JobStatus of(String value) {
        if (value == null) {
            return null;
        }
        for (JobStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
