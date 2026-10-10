package io.github.seed.module.job.data;

/**
 * 定时任务执行结果
 * <br>存储值即枚举名，日志表是给人查的，{@code where status = 'FAIL'} 直接可读
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum JobLogStatus {

    /**
     * 成功
     */
    SUCCESS("SUCCESS"),
    /**
     * 失败，message里记异常信息
     */
    FAIL("FAIL");

    /**
     * 存储值，显式写死而非取{@link #name()}——将来改枚举常量名不会连带改数据
     */
    private final String value;

    JobLogStatus(String value) {
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
    public static JobLogStatus of(String value) {
        if (value == null) {
            return null;
        }
        for (JobLogStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}
