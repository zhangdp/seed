package io.github.seed.service.sys;

import io.github.seed.entity.sys.JobNode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务集群节点服务
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface JobNodeService {

    /**
     * 写入或续期心跳，首次调用会插入节点记录
     *
     * @param node 节点信息
     */
    void heartbeat(JobNode node);

    /**
     * 查询心跳正常的节点
     *
     * @param heartbeatAfter 心跳时间晚于或等于该值才算存活
     * @return 存活节点id
     */
    List<String> listAliveNodeIds(LocalDateTime heartbeatAfter);

    /**
     * 清理长时间没心跳的节点
     *
     * @param heartbeatBefore 心跳时间早于该值的会被清理
     * @return 删除的条数
     */
    int deleteOffline(LocalDateTime heartbeatBefore);
}
