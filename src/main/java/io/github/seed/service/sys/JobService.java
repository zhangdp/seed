package io.github.seed.service.sys;

import io.github.seed.entity.sys.Job;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobQuery;
import io.github.seed.model.query.PageQuery;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务数据服务
 * <br>只负责按给定参数读写库：任务本身怎么校验cron、下次触发时间怎么算都在{@code JobManager}
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface JobService {

    /**
     * 分页查询
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    PageData<Job> queryPage(PageQuery<JobQuery> pageQuery);

    /**
     * 按id查询
     *
     * @param id 任务id
     * @return 任务，不存在时返回null
     */
    Job getById(Long id);

    /**
     * 新增
     *
     * @param job 任务
     * @return 是否新增成功
     */
    boolean add(Job job);

    /**
     * 修改
     *
     * @param job 任务
     * @return 是否修改成功
     */
    boolean update(Job job);

    /**
     * 删除
     *
     * @param id 任务id
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 更新状态与下次触发时间，启停任务时用
     *
     * @param id           任务id
     * @param status       目标状态
     * @param nextFireTime 下次触发时间，停止时可传null
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, String status, LocalDateTime nextFireTime);

    /**
     * 抢占一批到点的任务：先抢调度锁再挑任务，保证同一时刻只有一个节点在挑
     * <br>整段在一个事务里，锁随事务提交释放；返回的任务都已标记为执行中
     *
     * @param nodeId 抢占的节点id
     * @param limit  最多抢占多少条
     * @return 抢占到的任务
     */
    List<Job> claimDueJobs(String nodeId, int limit);

    /**
     * 按任务名称与分组查询，用于新增/修改时校验重名
     *
     * @param jobName  任务名称
     * @param jobGroup 任务分组
     * @return 已存在的任务，不存在时返回null
     */
    Job getByJobName(String jobName, String jobGroup);

    /**
     * 查询待触发的任务
     *
     * @param now   当前时间
     * @param limit 最大条数
     * @return 待触发的任务
     */
    List<Job> listFireable(LocalDateTime now, int limit);

    /**
     * 抢占任务，抢不到说明已被其他节点领走
     *
     * @param jobId   任务id
     * @param nodeId  抢占的节点id
     * @param firedAt 抢占时间
     * @return 是否抢占成功
     */
    boolean claim(Long jobId, String nodeId, LocalDateTime firedAt);

    /**
     * 回写一次触发的结果
     *
     * @param jobId        任务id
     * @param prevFireTime 本次触发时间
     * @param nextFireTime 下次触发时间
     * @param status       回写后的状态
     * @return 更新的行数
     */
    int finishFire(Long jobId, LocalDateTime prevFireTime, LocalDateTime nextFireTime, String status);

    /**
     * 查询抢占后迟迟没有回写的任务
     *
     * @param firedBefore 抢占时间早于该值的才算超时，为null表示不限
     * @return 待回收的任务
     */
    List<Job> listStaleFired(LocalDateTime firedBefore);
}
