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
    AUTO("AUTO"),
    /**
     * 在任务列表里点「执行一次」手动触发，不计入下次触发时间的推算
     */
    MANUAL("MANUAL");

    /**
     * 存储值，显式写死而非取{@link #name()}——将来改枚举常量名不会连带改数据
     */
    private final String value;

    JobTriggerType(String value) {
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
}
