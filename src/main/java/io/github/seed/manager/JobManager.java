package io.github.seed.manager;

import cn.hutool.v7.core.data.id.IdUtil;
import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Job;
import io.github.seed.entity.sys.JobLog;
import io.github.seed.entity.sys.JobNode;
import io.github.seed.module.job.JobProperties;
import io.github.seed.module.job.component.JobCronSupport;
import io.github.seed.module.job.component.JobInvoker;
import io.github.seed.module.job.data.JobLogStatus;
import io.github.seed.module.job.data.JobMisfirePolicy;
import io.github.seed.module.job.data.JobStatus;
import io.github.seed.module.job.data.JobTriggerType;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobLogQuery;
import io.github.seed.model.query.JobQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.JobLogService;
import io.github.seed.service.sys.JobNodeService;
import io.github.seed.service.sys.JobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务编排
 * <br>集群语义照quartz：挑任务前先抢数据库行锁，抢到任务的节点把它置为执行中，
 * 其余节点自然挑不到它；节点挂了没回写的任务由存活节点按心跳判断后接管
 * <br>任务执行放在虚拟线程里，避免单个任务耗时太久把整轮调度堵住
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobManager {

    private final JobService jobService;
    private final JobLogService jobLogService;
    private final JobNodeService jobNodeService;
    private final JobInvoker jobInvoker;
    private final JobCronSupport jobCronSupport;
    private final JobProperties jobProperties;
    private final Environment environment;

    /**
     * 主机名，仅用于展示
     */
    private final String hostName = this.resolveHostName();
    /**
     * 本节点标识，进程启动时生成，用于抢占标记与心跳
     */
    private final String nodeId = this.hostName + "-" + IdUtil.fastUUID().substring(0, 8);

    // ============================== 任务管理 ==============================

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
        // 未指定状态时按已停止入库：新增的任务不该默默就开始跑，由运维确认后再启用
        Integer status = job.getStatus() == null ? JobStatus.STOPPED.value() : job.getStatus();
        job.setStatus(status);
        job.setNextFireTime(status == JobStatus.WAITING.value()
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
        return this.executeAndLog(job, JobTriggerType.MANUAL).getStatus() == JobLogStatus.SUCCESS.value();
    }

    // ============================== 调度 ==============================

    /**
     * 挑出到点的任务并逐个执行，被{@code JobScheduleTask}定时驱动
     *
     * @return 本轮抢占到的任务数
     */
    public int fireDueJobs() {
        List<Job> claimed = jobService.claimDueJobs(this.nodeId, jobProperties.getFetchSize());
        if (claimed.isEmpty()) {
            return 0;
        }
        log.debug("本节点挑到{}个待触发任务，nodeId={}", claimed.size(), this.nodeId);
        for (Job job : claimed) {
            // 全局已开启虚拟线程，不另建线程池；任务互不影响，单个跑再久也不会堵住下一轮挑件
            Thread.ofVirtual().start(() -> this.fireOne(job));
        }
        return claimed.size();
    }

    /**
     * 执行并回写一次触发
     *
     * @param job 已抢占到的任务
     */
    private void fireOne(Job job) {
        JobLog jobLog = this.executeAndLog(job, JobTriggerType.AUTO);
        LocalDateTime next = this.nextFireTime(job.getCronExpression(), LocalDateTime.now());
        // 回写失败说明任务期间被停掉或已被接管，此时不该再改它的状态
        jobService.finishFire(job.getId(), job.getFiredAt(), next, JobStatus.WAITING.value());
        if (jobLog.getStatus() == JobLogStatus.FAIL.value()) {
            log.warn("任务执行失败：{}，原因：{}", job.getJobName(), jobLog.getMessage());
        }
    }

    /**
     * 执行任务并写执行日志
     *
     * @param job         任务
     * @param triggerType 触发方式
     * @return 执行日志
     */
    private JobLog executeAndLog(Job job, JobTriggerType triggerType) {
        LocalDateTime startedAt = LocalDateTime.now();
        long begin = System.currentTimeMillis();
        JobLog jobLog = new JobLog();
        jobLog.setJobId(job.getId());
        jobLog.setJobName(job.getJobName());
        jobLog.setJobGroup(job.getJobGroup());
        jobLog.setInvokeTarget(job.getInvokeTarget());
        jobLog.setTriggerType(triggerType.value());
        jobLog.setNodeId(this.nodeId);
        jobLog.setStartedAt(startedAt);
        try {
            jobInvoker.invoke(job.getInvokeTarget(), job.getParams());
            jobLog.setStatus(JobLogStatus.SUCCESS.value());
        } catch (Exception e) {
            jobLog.setStatus(JobLogStatus.FAIL.value());
            jobLog.setMessage(e.toString());
            log.error("任务执行异常：{}，目标：{}", job.getJobName(), job.getInvokeTarget(), e);
        } finally {
            jobLog.setEndedAt(LocalDateTime.now());
            jobLog.setDurationMs(System.currentTimeMillis() - begin);
            try {
                jobLogService.save(jobLog);
            } catch (Exception e) {
                // 日志写失败不能影响任务本身的结果
                log.error("任务执行日志保存失败：{}", job.getJobName(), e);
            }
        }
        return jobLog;
    }

    /**
     * 续期本节点心跳，同时清理长期离线的节点记录
     */
    public void heartbeat() {
        JobNode node = new JobNode();
        node.setNodeId(this.nodeId);
        node.setHostName(this.hostName);
        node.setPort(environment.getProperty("server.port", Integer.class));
        node.setLastHeartbeat(LocalDateTime.now());
        jobNodeService.heartbeat(node);
        // 离线超过10倍判定窗口的节点不会再被任何接管逻辑用到，清掉避免表无限增长
        jobNodeService.deleteOffline(LocalDateTime.now().minus(jobProperties.getNodeTimeout().multipliedBy(10)));
    }

    /**
     * 接管卡住的任务：抢占后节点就宕机了、或抢占时间已超过超时阈值
     * <br>按任务的补偿策略决定是立刻补跑一次还是跳到下次，同时记一条失败日志留痕
     *
     * @return 接管的任务数
     */
    public int takeoverStaleJobs() {
        LocalDateTime now = LocalDateTime.now();
        List<String> aliveNodeIds = jobNodeService.listAliveNodeIds(now.minus(jobProperties.getNodeTimeout()));
        List<Job> firing = jobService.listStaleFired(null);
        int count = 0;
        for (Job job : firing) {
            boolean nodeDead = job.getFiredBy() == null || !aliveNodeIds.contains(job.getFiredBy());
            boolean fireTimeout = job.getFiredAt() != null
                    && job.getFiredAt().isBefore(now.minus(jobProperties.getFireTimeout()));
            if (!nodeDead && !fireTimeout) {
                continue;
            }
            this.recover(job, now, nodeDead ? "执行节点已离线" : "执行超时未回写");
            count++;
        }
        return count;
    }

    /**
     * 回收单个卡住的任务
     *
     * @param job    卡住的任务
     * @param now    当前时间
     * @param reason 回收原因，写进执行日志
     */
    private void recover(Job job, LocalDateTime now, String reason) {
        JobLog jobLog = new JobLog();
        jobLog.setJobId(job.getId());
        jobLog.setJobName(job.getJobName());
        jobLog.setJobGroup(job.getJobGroup());
        jobLog.setInvokeTarget(job.getInvokeTarget());
        jobLog.setTriggerType(JobTriggerType.AUTO.value());
        jobLog.setNodeId(job.getFiredBy());
        jobLog.setStatus(JobLogStatus.FAIL.value());
        jobLog.setMessage(reason + "，已回收；无法确定任务是否真的执行过，按补偿策略处理");
        jobLog.setStartedAt(job.getFiredAt());
        jobLog.setEndedAt(now);
        try {
            jobLogService.save(jobLog);
        } catch (Exception e) {
            log.error("回收日志保存失败：{}", job.getJobName(), e);
        }
        // 立即补跑的策略把触发时间设为当前，下一轮就会再次触发；放弃策略直接跳到cron的下一次
        JobMisfirePolicy policy = JobMisfirePolicy.of(job.getMisfirePolicy());
        LocalDateTime next = policy == JobMisfirePolicy.FIRE_NOW ? now : this.nextFireTime(job.getCronExpression(), now);
        jobService.finishFire(job.getId(), job.getFiredAt(), next, JobStatus.WAITING.value());
        log.warn("已回收任务：{}，原因：{}，下次触发：{}", job.getJobName(), reason, next);
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

    // ============================== 私有 ==============================

    /**
     * 推算下次触发时间，同时校验cron是否合法
     *
     * @param cron cron表达式
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
     * 校验执行目标格式
     *
     * @param invokeTarget 执行目标
     */
    private void checkTarget(String invokeTarget) {
        if (invokeTarget == null || !invokeTarget.contains(".")) {
            throw new BizException(ErrorCode.JOB_TARGET_INVALID);
        }
    }

    /**
     * 生成本节点标识
     *
     * @return 节点id
     */
    private String generateNodeId() {
        return this.resolveHostName() + "-" + IdUtil.fastUUID().substring(0, 8);
    }

    /**
     * 取主机名，取不到时用unknown兜底，不能因为取主机名失败就起不来
     *
     * @return 主机名
     */
    private String resolveHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
