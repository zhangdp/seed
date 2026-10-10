package io.github.seed.service.sys.impl;

import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.JobNode;
import io.github.seed.mapper.sys.JobNodeMapper;
import io.github.seed.service.sys.JobNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务集群节点服务实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class JobNodeServiceImpl implements JobNodeService {

    private final JobNodeMapper jobNodeMapper;

    @Override
    public void heartbeat(JobNode node) {
        if (jobNodeMapper.heartbeat(node) == 0) {
            node.setCreatedAt(LocalDateTime.now());
            jobNodeMapper.insert(node);
        }
    }

    @Override
    public List<String> listAliveNodeIds(LocalDateTime heartbeatAfter) {
        return jobNodeMapper.listAliveNodeIds(heartbeatAfter);
    }

    @Override
    public int deleteOffline(LocalDateTime heartbeatBefore) {
        return jobNodeMapper.deleteByQuery(QueryWrapper.create().lt(JobNode::getLastHeartbeat, heartbeatBefore));
    }
}
