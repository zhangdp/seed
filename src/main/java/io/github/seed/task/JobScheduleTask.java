package io.github.seed.task;

import io.github.seed.manager.JobManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务调度
 * <br>只做定时驱动，不关心任务怎么抢、怎么跑：到点了就叫{@link JobManager}去挑任务
 * <br>三个动作共用一个调度线程串行执行，挑到任务后真正的执行在虚拟线程里，不会堵住这里
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class JobScheduleTask {

    private final JobManager jobManager;

    /**
     * 挑出到点的任务并触发；间隔由{@code app.job.poll-interval}控制
     */
    @Scheduled(initialDelay = 10_000L, fixedDelayString = "${app.job.poll-interval:PT3S}")
    public void fireDueJobs() {
        try {
            jobManager.fireDueJobs();
        } catch (Exception e) {
            // 挑件失败不能让调度线程死掉，否则本节点从此不再触发任何任务
            log.error("挑任务失败，本轮跳过", e);
        }
    }

    /**
     * 续期节点心跳；间隔由{@code app.job.heartbeat-interval}控制
     */
    @Scheduled(initialDelay = 5_000L, fixedDelayString = "${app.job.heartbeat-interval:PT15S}")
    public void heartbeat() {
        try {
            jobManager.heartbeat();
        } catch (Exception e) {
            log.error("节点心跳续期失败，本轮跳过", e);
        }
    }

    /**
     * 接管卡住的任务：抢占它们的节点已离线或抢占时间已超时
     */
    @Scheduled(initialDelay = 20_000L, fixedDelayString = "${app.job.heartbeat-interval:PT15S}")
    public void takeoverStaleJobs() {
        try {
            int count = jobManager.takeoverStaleJobs();
            if (count > 0) {
                log.warn("本轮接管了{}个卡住的任务", count);
            }
        } catch (Exception e) {
            log.error("接管卡住任务失败，本轮跳过", e);
        }
    }
}
