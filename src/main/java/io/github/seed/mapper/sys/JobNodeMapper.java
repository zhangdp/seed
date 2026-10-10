package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.JobNode;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务集群节点mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface JobNodeMapper extends BaseMapper<JobNode> {

    /**
     * 写入或续期心跳
     *
     * @param node 节点信息，nodeId为主键
     * @return 更新的行数，0表示首次心跳需要插入
     */
    default int heartbeat(JobNode node) {
        return this.update(node);
    }

    /**
     * 查询心跳正常的节点
     *
     * @param heartbeatAfter 心跳时间晚于或等于该值才算存活
     * @return 存活节点id
     */
    default List<String> listAliveNodeIds(LocalDateTime heartbeatAfter) {
        List<JobNode> nodes = this.selectListByQuery(QueryWrapper.create()
                .ge(JobNode::getLastHeartbeat, heartbeatAfter)
                .select(JobNode::getNodeId));
        return nodes.stream().map(JobNode::getNodeId).toList();
    }
}
