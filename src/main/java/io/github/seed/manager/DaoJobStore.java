package io.github.seed.manager;

import cn.hutool.v7.core.bean.BeanUtil;
import io.github.seed.entity.sys.Job;
import io.github.seed.entity.sys.JobLog;
import io.github.seed.entity.sys.JobNode;
import io.github.seed.module.job.data.JobData;
import io.github.seed.module.job.data.JobLogData;
import io.github.seed.module.job.data.JobNodeData;
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
 * <br>转换方向：出参{@code Job}->{@link JobData}，入参{@link JobNodeData}/{@link JobLogData}->{@code JobNode}/{@code JobLog}，
 * 两个方向的字段名保持一致，所以直接用{@link BeanUtil}按名拷贝，不用逐字段搬运
 * <br>Dao前缀表明数据取自数据库，与{@code DaoUserDetailsService}同一命名习惯
 * <br>{@link #saveLog(JobLogData)}必须吞掉异常：日志写失败不能让任务的执行结果回不去
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
    public List<JobData> claimDueJobs(String nodeId, int limit) {
        return jobService.claimDueJobs(nodeId, limit).stream().map(DaoJobStore::toData).toList();
    }

    @Override
    public int finishFire(Long jobId, LocalDateTime prevFireTime, LocalDateTime nextFireTime, String status) {
        return jobService.finishFire(jobId, prevFireTime, nextFireTime, status);
    }

    @Override
    public List<JobData> listStaleFired(LocalDateTime firedBefore) {
        return jobService.listStaleFired(firedBefore).stream().map(DaoJobStore::toData).toList();
    }

    @Override
    public void heartbeat(JobNodeData node) {
        JobNode entity = new JobNode();
        BeanUtil.copyProperties(node, entity);
        jobNodeService.heartbeat(entity);
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
    public void saveLog(JobLogData jobLog) {
        JobLog entity = new JobLog();
        BeanUtil.copyProperties(jobLog, entity);
        try {
            jobLogService.save(entity);
        } catch (Exception e) {
            // 日志写失败不能影响任务本身的结果
            log.error("任务执行日志保存失败：{}", jobLog.getJobName(), e);
        }
    }

    /**
     * 转到模块的数据对象，字段名一致按名拷贝
     *
     * @param job 任务
     * @return 模块视角的任务
     */
    private static JobData toData(Job job) {
        JobData data = new JobData();
        BeanUtil.copyProperties(job, data);
        return data;
    }
}
