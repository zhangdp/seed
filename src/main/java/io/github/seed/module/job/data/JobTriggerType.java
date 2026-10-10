package io.github.seed.module.job.data;

/**
 * 定时任务的触发方式
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum JobTriggerType {

    /**
     * 调度器按cron自动触发
     */
    AUTO(0),
    /**
     * 在任务列表里点「执行一次」手动触发，不计入下次触发时间的推算
     */
    MANUAL(1);

    private final int value;

    JobTriggerType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
}
