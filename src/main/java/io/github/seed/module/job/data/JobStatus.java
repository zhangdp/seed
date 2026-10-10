package io.github.seed.module.job.data;

/**
 * 定时任务状态
 * <br>对应quartz里trigger的状态：已停止=PAUSED、待触发=WAITING、执行中=ACQUIRED
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum JobStatus {

    /**
     * 已停止，调度器不挑它
     */
    STOPPED(0),
    /**
     * 待触发，到了next_fire_time就会被抢占执行
     */
    WAITING(1),
    /**
     * 执行中，已被某个节点抢占；任务跑完或超时回收后回到其他状态
     */
    FIRING(2);

    private final int value;

    JobStatus(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    /**
     * 按存储值取枚举，取不到时返回null
     *
     * @param value 存储值
     * @return 对应的枚举
     */
    public static JobStatus of(Integer value) {
        if (value == null) {
            return null;
        }
        for (JobStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }
}
