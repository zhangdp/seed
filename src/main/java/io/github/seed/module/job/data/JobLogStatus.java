package io.github.seed.module.job.data;

/**
 * 定时任务执行结果
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum JobLogStatus {

    /**
     * 成功
     */
    SUCCESS(0),
    /**
     * 失败，message里记异常信息
     */
    FAIL(1);

    private final int value;

    JobLogStatus(int value) {
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
    public static JobLogStatus of(Integer value) {
        if (value == null) {
            return null;
        }
        return value == SUCCESS.value ? SUCCESS : (value == FAIL.value ? FAIL : null);
    }
}
