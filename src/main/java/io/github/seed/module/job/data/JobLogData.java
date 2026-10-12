package io.github.seed.module.job.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 一次执行的日志
 * <br>冗余存任务名、执行目标等：任务删了之后日志还要能看明白当时跑了什么
 * <br>模块自带数据对象而不是直接用{@code entity}：换库或改字段只改宿主的{@code JobStore}实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
public class JobLogData {

    /**
     * 任务id
     */
    private Long jobId;
    /**
     * 任务名
     */
    private String jobName;
    /**
     * 任务分组
     */
    private String jobGroup;
    /**
     * 执行目标，{@code bean名.方法名}
     */
    private String invokeTarget;
    /**
     * 触发方式，见{@link JobTriggerType}
     */
    private String triggerType;
    /**
     * 执行节点id
     */
    private String nodeId;
    /**
     * 执行结果，见{@link JobLogStatus}
     */
    private String status;
    /**
     * 失败原因
     */
    private String message;
    /**
     * 开始时间
     */
    private LocalDateTime startedAt;
    /**
     * 结束时间
     */
    private LocalDateTime endedAt;
    /**
     * 耗时毫秒数
     */
    private Long durationMs;
}
