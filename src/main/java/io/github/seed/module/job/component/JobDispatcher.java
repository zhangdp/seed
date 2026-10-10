package io.github.seed.module.job.component;

import cn.hutool.v7.core.data.id.IdUtil;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Job;
import io.github.seed.entity.sys.JobLog;
import io.github.seed.entity.sys.JobNode;
import io.github.seed.module.job.JobProperties;
import io.github.seed.module.job.data.JobLogStatus;
import io.github.seed.module.job.data.JobMisfirePolicy;
import io.github.seed.module.job.data.JobStatus;
import io.github.seed.module.job.data.JobTriggerType;
import io.github.seed.module.job.store.JobStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务调度引擎：挑任务、执行、续期心跳、接管卡住的任务
 * <br>集群语义照quartz：挑任务前先抢数据库行锁，抢到任务的节点把它置为执行中，其余节点自然挑不到它；
 * 节点挂了没回写的任务由存活节点按心跳判断后接管
 * <br>只依赖{@link JobStore}接口与模块内的能力类，不认识{@code manager}也不认识{@code service}，
 * 数据读写全走宿主注入进来的存储实现
 * <br>任务执行放在虚拟线程里，避免单个任务耗时太久把整轮调度堵住
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class JobDispatcher {

    private final JobStore jobStore;
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

    /**
     * 挑出到点的任务并逐个执行，被{@code JobScheduleTask}定时驱动
     *
     * @return 本轮抢占到的任务数
     */
    public int fireDueJobs() {
        List<Job> claimed = jobStore.claimDueJobs(this.nodeId, jobProperties.getFetchSize());
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
     * 执行任务并写执行日志
     *
     * @param job         任务
     * @param triggerType 触发方式
     * @return 执行日志
     */
    public JobLog execute(Job job, JobTriggerType triggerType) {
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
            jobStore.saveLog(jobLog);
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
        jobStore.heartbeat(node);
        // 离线超过10倍判定窗口的节点不会再被任何接管逻辑用到，清掉避免表无限增长
        jobStore.deleteOffline(LocalDateTime.now().minus(jobProperties.getNodeTimeout().multipliedBy(10)));
    }

    /**
     * 接管卡住的任务：抢占后节点就宕机了、或抢占时间已超过超时阈值
     * <br>按任务的补偿策略决定是立刻补跑一次还是跳到下次，同时记一条失败日志留痕
     *
     * @return 接管的任务数
     */
    public int takeoverStaleJobs() {
        LocalDateTime now = LocalDateTime.now();
        List<String> aliveNodeIds = jobStore.listAliveNodeIds(now.minus(jobProperties.getNodeTimeout()));
        List<Job> firing = jobStore.listStaleFired(null);
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
     * 执行并回写一次自动触发
     *
     * @param job 已抢占到的任务
     */
    private void fireOne(Job job) {
        JobLog jobLog = this.execute(job, JobTriggerType.AUTO);
        LocalDateTime next = this.nextFireTime(job.getCronExpression(), LocalDateTime.now());
        // 回写失败说明任务期间被停掉或已被接管，此时不该再改它的状态
        jobStore.finishFire(job.getId(), job.getFiredAt(), next, JobStatus.WAITING.value());
        if (JobLogStatus.FAIL.value().equals(jobLog.getStatus())) {
            log.warn("任务执行失败：{}，原因：{}", job.getJobName(), jobLog.getMessage());
        }
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
        jobStore.saveLog(jobLog);
        // 立即补跑的策略把触发时间设为当前，下一轮就会再次触发；放弃策略直接跳到cron的下一次
        JobMisfirePolicy policy = JobMisfirePolicy.of(job.getMisfirePolicy());
        LocalDateTime next = policy == JobMisfirePolicy.FIRE_NOW ? now : this.nextFireTime(job.getCronExpression(), now);
        jobStore.finishFire(job.getId(), job.getFiredAt(), next, JobStatus.WAITING.value());
        log.warn("已回收任务：{}，原因：{}，下次触发：{}", job.getJobName(), reason, next);
    }

    /**
     * 推算下次触发时间，cron非法时抛业务异常
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
