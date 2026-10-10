package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务执行日志
 * <br>任务被删除后日志仍要能查，所以任务名称等字段冗余存一份，且不建外键
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_JOB_LOG)
@Schema(description = "定时任务执行日志")
public class JobLog extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 异常信息的最大存储长度，超出截断，堆栈本身不入库
     */
    public static final int MAX_MESSAGE_LENGTH = 2048;

    /**
     * 任务id
     */
    @Schema(description = "任务id")
    private Long jobId;
    /**
     * 任务名称
     */
    @Schema(description = "任务名称")
    private String jobName;
    /**
     * 任务分组
     */
    @Schema(description = "任务分组")
    private String jobGroup;
    /**
     * 执行目标
     */
    @Schema(description = "执行目标")
    private String invokeTarget;
    /**
     * 触发方式：0自动、1手动，见{@code JobTriggerType}
     */
    @Schema(description = "触发方式：0自动、1手动")
    private Integer triggerType;
    /**
     * 执行节点
     */
    @Schema(description = "执行节点")
    private String nodeId;
    /**
     * 执行结果：0成功、1失败，见{@code JobLogStatus}
     */
    @Schema(description = "执行结果：0成功、1失败")
    private Integer status;
    /**
     * 执行结果描述，失败时为异常信息
     */
    @Schema(description = "执行结果描述")
    private String message;
    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private LocalDateTime startedAt;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private LocalDateTime endedAt;
    /**
     * 耗时（毫秒）
     */
    @Schema(description = "耗时（毫秒）")
    private Long durationMs;
}
