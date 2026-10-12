package io.github.seed.module.job.data;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 一个调度节点的心跳
 * <br>模块自带数据对象而不是直接用{@code entity}：换库或改字段只改宿主的{@code JobStore}实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
public class JobNodeData {

    /**
     * 节点id，进程启动时生成
     */
    private String nodeId;
    /**
     * 主机名，仅用于展示
     */
    private String hostName;
    /**
     * 服务端口，仅用于展示
     */
    private Integer port;
    /**
     * 最近一次心跳时间
     */
    private LocalDateTime lastHeartbeat;
}
