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
 * 定时任务
 * <br>任务定义与触发信息合并在一张表里（quartz里是job_detail与trigger两张），
 * 一个任务只配一个cron，够用且省掉一次关联查询
 * <br>集群语义与quartz一致：{@code status=2}且{@code fired_by/fired_at}已写入表示被某节点抢占，
 * 其余节点不会再挑它
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_JOB)
@Schema(description = "定时任务")
public class Job extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称，同一分组下唯一
     */
    @Schema(description = "任务名称")
    private String jobName;
    /**
     * 任务分组，预留给按组批量启停
     */
    @Schema(description = "任务分组")
    private String jobGroup;
    /**
     * 执行目标，格式beanName.methodName，调度时按名字取spring bean反射调用
     */
    @Schema(description = "执行目标，格式beanName.methodName")
    private String invokeTarget;
    /**
     * cron表达式，6位：秒 分 时 日 月 周
     */
    @Schema(description = "cron表达式")
    private String cronExpression;
    /**
     * 方法参数，json数组；无参方法留空
     */
    @Schema(description = "方法参数，json数组")
    private String params;
    /**
     * 状态：0已停止、1待触发、2执行中，见{@code JobStatus}
     */
    @Schema(description = "状态：0已停止、1待触发、2执行中")
    private Integer status;
    /**
     * 错过触发时的策略：0立即补跑、1放弃，见{@code JobMisfirePolicy}
     */
    @Schema(description = "错过触发时的策略：0立即补跑、1放弃")
    private Integer misfirePolicy;
    /**
     * 下一次触发时间
     */
    @Schema(description = "下一次触发时间")
    private LocalDateTime nextFireTime;
    /**
     * 上一次触发时间
     */
    @Schema(description = "上一次触发时间")
    private LocalDateTime prevFireTime;
    /**
     * 本次执行被哪个节点抢占
     */
    @Schema(description = "执行节点")
    private String firedBy;
    /**
     * 抢占时间，节点中途宕机未回写时据此超时回收
     */
    @Schema(description = "抢占时间")
    private LocalDateTime firedAt;
    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
