package io.github.seed.module.job.store;

import io.github.seed.entity.sys.Job;
import io.github.seed.entity.sys.JobLog;
import io.github.seed.entity.sys.JobNode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 调度引擎需要的存储能力，由宿主应用实现后注入
 * <br>模块只认本接口，不认识{@code service}也不认识表：换一套存储（换库、或换成内存实现跑单测）
 * 只要另写一个实现类，调度引擎一行不用改
 * <br>实现方要保证{@link #saveLog(JobLog)}不抛异常——日志写失败不能影响任务本身的结果
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface JobStore {

    /**
     * 抢占一批到点的任务，返回的任务都已标记为执行中
     *
     * @param nodeId 抢占的节点id
     * @param limit  本轮最多抢占多少条
     * @return 抢占到的任务，为空表示本轮无任务或已被其他节点领走
     */
    List<Job> claimDueJobs(String nodeId, int limit);

    /**
     * 回写一次触发的结果
     *
     * @param jobId        任务id
     * @param prevFireTime 本次触发时间
     * @param nextFireTime 下次触发时间
     * @param status       回写后的状态
     * @return 更新的行数，为0说明任务期间已被停掉或已被接管
     */
    int finishFire(Long jobId, LocalDateTime prevFireTime, LocalDateTime nextFireTime, String status);

    /**
     * 查询抢占后迟迟没有回写的任务
     *
     * @param firedBefore 抢占时间早于该值的才算超时，为null表示不限
     * @return 待回收的任务
     */
    List<Job> listStaleFired(LocalDateTime firedBefore);

    /**
     * 写入或续期节点心跳，首次调用会插入节点记录
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

    /**
     * 保存一条执行日志，保存失败只记日志不抛出
     *
     * @param jobLog 执行日志
     */
    void saveLog(JobLog jobLog);
}
