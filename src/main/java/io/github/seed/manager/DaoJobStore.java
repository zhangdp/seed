package io.github.seed.manager;

import io.github.seed.entity.sys.Job;
import io.github.seed.entity.sys.JobLog;
import io.github.seed.entity.sys.JobNode;
import io.github.seed.module.job.store.JobStore;
import io.github.seed.service.sys.JobLogService;
import io.github.seed.service.sys.JobNodeService;
import io.github.seed.service.sys.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调度引擎所需存储能力的数据库实现：把三个数据服务聚合成{@link JobStore}要求的形状
 * <br>放在{@code manager}是因为它干的就是「聚合多个service」——模块只认接口，数据落在哪由宿主决定；
 * 换存储（换成内存实现跑单测）只要另写一个实现类替换掉本类
 * <br>Dao前缀表明数据取自数据库，与{@code module/security}的{@code DaoUserDetailsService}同一命名习惯
 * <br>{@link #saveLog(JobLog)}必须吞掉异常：日志写失败不能让任务的执行结果回不去
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DaoJobStore implements JobStore {

    private final JobService jobService;
    private final JobLogService jobLogService;
    private final JobNodeService jobNodeService;

    @Override
    public List<Job> claimDueJobs(String nodeId, int limit) {
        return jobService.claimDueJobs(nodeId, limit);
    }

    @Override
    public int finishFire(Long jobId, LocalDateTime prevFireTime, LocalDateTime nextFireTime, String status) {
        return jobService.finishFire(jobId, prevFireTime, nextFireTime, status);
    }

    @Override
    public List<Job> listStaleFired(LocalDateTime firedBefore) {
        return jobService.listStaleFired(firedBefore);
    }

    @Override
    public void heartbeat(JobNode node) {
        jobNodeService.heartbeat(node);
    }

    @Override
    public List<String> listAliveNodeIds(LocalDateTime heartbeatAfter) {
        return jobNodeService.listAliveNodeIds(heartbeatAfter);
    }

    @Override
    public int deleteOffline(LocalDateTime heartbeatBefore) {
        return jobNodeService.deleteOffline(heartbeatBefore);
    }

    @Override
    public void saveLog(JobLog jobLog) {
        try {
            jobLogService.save(jobLog);
        } catch (Exception e) {
            // 日志写失败不能影响任务本身的结果
            log.error("任务执行日志保存失败：{}", jobLog.getJobName(), e);
        }
    }
}
