package io.github.seed.service.sys.impl;

import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Job;
import io.github.seed.mapper.sys.JobMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.module.job.data.JobStatus;
import io.github.seed.service.sys.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时任务数据服务实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

    private final JobMapper jobMapper;

    @Override
    public PageData<Job> queryPage(PageQuery<JobQuery> pageQuery) {
        return jobMapper.selectPage(pageQuery);
    }

    @Override
    public Job getById(Long id) {
        return jobMapper.selectOneById(id);
    }

    @Override
    public boolean add(Job job) {
        return jobMapper.insert(job) > 0;
    }

    @Override
    public boolean update(Job job) {
        return jobMapper.update(job) > 0;
    }

    @Override
    public boolean delete(Long id) {
        return jobMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Long id, String status, LocalDateTime nextFireTime) {
        return jobMapper.updateStatus(id, status, nextFireTime) > 0;
    }

    @Override
    @Transactional
    public List<Job> claimDueJobs(String nodeId, int limit) {
        // 行锁挡住其他节点的挑件动作，锁在事务提交时释放
        jobMapper.lockTriggerAccess(JobMapper.TRIGGER_ACCESS);
        LocalDateTime now = LocalDateTime.now();
        List<Job> fireable = jobMapper.listFireable(now, limit);
        List<Job> claimed = new ArrayList<>(fireable.size());
        for (Job job : fireable) {
            if (jobMapper.claim(job.getId(), nodeId, now)) {
                job.setStatus(JobStatus.FIRING.value());
                job.setFiredBy(nodeId);
                job.setFiredAt(now);
                claimed.add(job);
            }
        }
        return claimed;
    }

    @Override
    public Job getByJobName(String jobName, String jobGroup) {
        return jobMapper.selectOneByQuery(QueryWrapper.create()
                .eq(Job::getJobName, jobName)
                .eq(Job::getJobGroup, jobGroup));
    }

    @Override
    public List<Job> listFireable(LocalDateTime now, int limit) {
        return jobMapper.listFireable(now, limit);
    }

    @Override
    public boolean claim(Long jobId, String nodeId, LocalDateTime firedAt) {
        return jobMapper.claim(jobId, nodeId, firedAt);
    }

    @Override
    public int finishFire(Long jobId, LocalDateTime prevFireTime, LocalDateTime nextFireTime, String status) {
        return jobMapper.finishFire(jobId, prevFireTime, nextFireTime, status);
    }

    @Override
    public List<Job> listStaleFired(LocalDateTime firedBefore) {
        return jobMapper.listStaleFired(firedBefore);
    }
}
