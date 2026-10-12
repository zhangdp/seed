package io.github.seed.module.job.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 调度引擎眼里的一个任务
 * <br>只放调度用得到的字段：{@code remark}这类档案信息与调度无关，不跟着传来传去
 * <br>模块自带数据对象而不是直接用{@code entity}：调度引擎不该认识宿主的表结构，
 * 换库或改字段只改宿主的{@code JobStore}实现，模块一行不动
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
public class JobData {

    /**
     * 任务id
     */
    private Long id;
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
     * cron表达式
     */
    private String cronExpression;
    /**
     * 执行参数，json字符串
     */
    private String params;
    /**
     * 错过触发时的补偿策略，见{@link JobMisfirePolicy}
     */
    private String misfirePolicy;
    /**
     * 抢占本轮执行的节点id
     */
    private String firedBy;
    /**
     * 本轮抢占时间
     */
    private LocalDateTime firedAt;
}
