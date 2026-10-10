package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 定时任务集群节点
 * <br>各节点定期续期last_heartbeat，长时间不续期的节点视为已下线，
 * 它抢占了却没跑完的任务由存活节点接管，避免任务卡在执行中状态再也不触发
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Table(TableNameConst.SYS_JOB_NODE)
@Schema(description = "定时任务集群节点")
public class JobNode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 节点唯一标识，应用启动时生成
     */
    @Id
    @Schema(description = "节点id")
    private String nodeId;
    /**
     * 主机名
     */
    @Schema(description = "主机名")
    private String hostName;
    /**
     * 服务端口
     */
    @Schema(description = "服务端口")
    private Integer port;
    /**
     * 最近一次心跳时间
     */
    @Schema(description = "最近一次心跳时间")
    private LocalDateTime lastHeartbeat;
    /**
     * 首次心跳时间
     */
    @Schema(description = "注册时间")
    private LocalDateTime createdAt;
}
