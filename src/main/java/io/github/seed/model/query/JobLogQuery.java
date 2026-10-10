package io.github.seed.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务执行日志查询入参，条件都可空（空值条件由MyBatis-Flex自动忽略）
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(description = "定时任务执行日志查询入参")
public class JobLogQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务id
     */
    @Schema(title = "任务id")
    private Long jobId;
    /**
     * 任务名称，模糊匹配
     */
    @Schema(title = "任务名称")
    private String jobName;
    /**
     * 执行结果：SUCCESS成功、FAIL失败
     */
    @Schema(title = "执行结果", description = "SUCCESS成功、FAIL失败")
    private String status;
    /**
     * 触发方式：AUTO自动、MANUAL手动
     */
    @Schema(title = "触发方式", description = "AUTO自动、MANUAL手动")
    private String triggerType;
    /**
     * 开始时间范围起点，按started_at过滤
     */
    @Schema(title = "开始时间起")
    private String startTime;
    /**
     * 开始时间范围终点，按started_at过滤
     */
    @Schema(title = "开始时间止")
    private String endTime;
}
