package io.github.seed.manager;

import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Job;
import io.github.seed.entity.sys.JobLog;
import io.github.seed.module.job.component.JobCronSupport;
import io.github.seed.module.job.component.JobDispatcher;
import io.github.seed.module.job.data.JobLogStatus;
import io.github.seed.module.job.data.JobMisfirePolicy;
import io.github.seed.module.job.data.JobStatus;
import io.github.seed.module.job.data.JobTriggerType;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobLogQuery;
import io.github.seed.model.query.JobQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.JobLogService;
import io.github.seed.service.sys.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务编排：任务的增删改查、启停、手动执行与执行日志查询
 * <br>只做业务编排与入参校验；「怎么抢任务、怎么执行、节点心跳、接管卡住的任务」都在{@link JobDispatcher}
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class JobManager {

    private final JobService jobService;
    private final JobLogService jobLogService;
    private final JobDispatcher jobDispatcher;
    private final JobCronSupport jobCronSupport;

    /**
     * 分页查询任务
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    public PageData<Job> queryPage(PageQuery<JobQuery> pageQuery) {
        return jobService.queryPage(pageQuery);
    }

    /**
     * 按id查询任务
     *
     * @param id 任务id
     * @return 任务
     */
    public Job getById(Long id) {
        Job job = jobService.getById(id);
        Assert.notNull(job, () -> new BizException(ErrorCode.JOB_NOT_EXISTS));
        return job;
    }

    /**
     * 新增任务：校验执行目标与cron，并算出首次触发时间
     *
     * @param job 任务
     * @return 是否新增成功
     */
    public boolean add(Job job) {
        this.checkTarget(job.getInvokeTarget());
        this.checkCron(job.getCronExpression());
        // 未指定状态时按已停止入库：新增的任务不该默默就开始跑，由运维确认后再启用
        String status = job.getStatus() == null ? JobStatus.STOPPED.value() : job.getStatus();
        job.setStatus(status);
        job.setMisfirePolicy(job.getMisfirePolicy() == null ? JobMisfirePolicy.SKIP.value() : job.getMisfirePolicy());
        job.setNextFireTime(JobStatus.WAITING.value().equals(status)
                ? this.nextFireTime(job.getCronExpression(), LocalDateTime.now()) : null);
        Assert.isTrue(jobService.getByJobName(job.getJobName(), job.getJobGroup()) == null,
                () -> new BizException(ErrorCode.JOB_NAME_REPEAT));
        return jobService.add(job);
    }

    /**
     * 修改任务：cron变了要重算下次触发时间，否则沿用旧的
     *
     * @param job 任务
     * @return 是否修改成功
     */
    public boolean update(Job job) {
        Job exists = this.getById(job.getId());
        this.checkTarget(job.getInvokeTarget());
        // cron不传表示不改，传了就得是合法的
        if (job.getCronExpression() != null) {
            this.checkCron(job.getCronExpression());
        }
        if (!exists.getJobName().equals(job.getJobName()) || !exists.getJobGroup().equals(job.getJobGroup())) {
            Assert.isTrue(jobService.getByJobName(job.getJobName(), job.getJobGroup()) == null,
                    () -> new BizException(ErrorCode.JOB_NAME_REPEAT));
        }
        // cron变了要重算下次触发时间；没变就沿用库里的，免得改个备注就把下次执行时间推后了
        boolean cronChanged = job.getCronExpression() != null
                && !job.getCronExpression().equals(exists.getCronExpression());
        job.setNextFireTime(cronChanged ? this.nextFireTime(job.getCronExpression(), LocalDateTime.now()) : null);
        // 状态、抢占信息都走专门接口改，普通修改不能动它们
        job.setStatus(null);
        job.setFiredBy(null);
        job.setFiredAt(null);
        return jobService.update(job);
    }

    /**
     * 删除任务
     *
     * @param id 任务id
     * @return 是否删除成功
     */
    public boolean delete(Long id) {
        this.getById(id);
        return jobService.delete(id);
    }

    /**
     * 启用或停止任务：启用时从当前时间推算下次触发时间，停止时清空它
     *
     * @param id    任务id
     * @param start true启用、false停止
     * @return 是否更新成功
     */
    public boolean changeStatus(Long id, boolean start) {
        Job job = this.getById(id);
        LocalDateTime nextFireTime = start ? this.nextFireTime(job.getCronExpression(), LocalDateTime.now()) : null;
        return jobService.updateStatus(id, start ? JobStatus.WAITING.value() : JobStatus.STOPPED.value(), nextFireTime);
    }

    /**
     * 立即执行一次：不抢占、不改动任务状态与触发时间，只记一条手动触发的日志
     *
     * @param id 任务id
     * @return 是否执行成功，任务不存在时抛业务异常
     */
    public boolean runOnce(Long id) {
        Job job = this.getById(id);
        return JobLogStatus.SUCCESS.value().equals(jobDispatcher.execute(job, JobTriggerType.MANUAL).getStatus());
    }

    /**
     * 分页查询执行日志
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    public PageData<JobLog> queryLogPage(PageQuery<JobLogQuery> pageQuery) {
        return jobLogService.queryPage(pageQuery);
    }

    /**
     * 清空执行日志
     *
     * @param jobId 任务id，为空表示清空全部
     * @return 删除的条数
     */
    public int clearLog(Long jobId) {
        return jobId == null ? jobLogService.clear() : jobLogService.deleteByJobId(jobId);
    }

    /**
     * 推算下次触发时间，同时校验cron是否合法
     *
     * @param cron  cron表达式
     * @param after 推算起点
     * @return 下次触发时间
     */
    private LocalDateTime nextFireTime(String cron, LocalDateTime after) {
        if (cron == null || cron.isBlank()) {
            throw new BizException(ErrorCode.JOB_CRON_INVALID);
        }
        try {
            return jobCronSupport.next(cron, after);
        } catch (IllegalArgumentException e) {
            throw new BizException(ErrorCode.JOB_CRON_INVALID);
        }
    }

    /**
     * 校验cron是否合法，不合法抛业务异常
     *
     * @param cron cron表达式
     */
    private void checkCron(String cron) {
        if (cron == null || cron.isBlank()) {
            throw new BizException(ErrorCode.JOB_CRON_INVALID);
        }
        try {
            jobCronSupport.parse(cron);
        } catch (IllegalArgumentException e) {
            throw new BizException(ErrorCode.JOB_CRON_INVALID);
        }
    }

    /**
     * 校验执行目标格式
     *
     * @param invokeTarget 执行目标
     */
    private void checkTarget(String invokeTarget) {
        if (invokeTarget == null || !invokeTarget.contains(".")) {
            throw new BizException(ErrorCode.JOB_TARGET_INVALID);
        }
    }
}
