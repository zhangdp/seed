package io.github.seed.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时任务查询入参，条件都可空（空值条件由MyBatis-Flex自动忽略）
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(description = "定时任务查询入参")
public class JobQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 任务名称，模糊匹配
     */
    @Schema(title = "任务名称")
    private String jobName;
    /**
     * 任务分组
     */
    @Schema(title = "任务分组")
    private String jobGroup;
    /**
     * 状态：0已停止、1待触发、2执行中
     */
    @Schema(title = "状态", description = "0已停止、1待触发、2执行中")
    private Integer status;
}
