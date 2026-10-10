package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Job;
import io.github.seed.module.job.data.JobStatus;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobQuery;
import io.github.seed.model.query.PageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务mapper
 * <br>抢占与回收都走条件更新，靠数据库保证多节点互斥，不依赖应用内锁
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface JobMapper extends BaseMapper<Job> {

    /**
     * 调度锁名称，对应sys_job_lock里预置的那一行
     */
    String TRIGGER_ACCESS = "TRIGGER_ACCESS";
    /**
     * 分页查询未指定排序时的默认排序
     */
    String DEFAULT_ORDER_BY = "id asc";
    /**
     * 可触发的条件：已到触发时间，或压根没设过触发时间
     * <br>后者是兜底：从库里直接改状态启用、或种子数据没算触发时间时，不该让任务永远躺着不跑
     */
    String FIREABLE_CONDITION = "next_fire_time <= ? or next_fire_time is null";

    /**
     * 抢占调度锁：对锁行加行级排他锁，保证同一时刻只有一个节点在挑任务
     * <br>必须在事务内调用，事务提交时锁才释放；锁不住时会阻塞直到持锁事务结束
     *
     * @param lockName 锁名称
     * @return 锁名称，拿不到时为null
     */
    @Select("select lock_name from sys_job_lock where lock_name = #{lockName} for update")
    String lockTriggerAccess(@Param("lockName") String lockName);

    /**
     * 查询分页
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    default PageData<Job> selectPage(PageQuery<JobQuery> pageQuery) {
        return this.selectPage(pageQuery, this.buildQueryWrapper(pageQuery.getParams()));
    }

    /**
     * 查询待触发的任务：状态为待触发且已到触发时间
     * <br>调用前需先持有调度锁
     *
     * @param now   当前时间，触发时间早于或等于它的都会被挑出来
     * @param limit 最大条数
     * @return 待触发的任务
     */
    default List<Job> listFireable(LocalDateTime now, int limit) {
        return this.selectListByQuery(QueryWrapper.create()
                .eq(Job::getStatus, JobStatus.WAITING.value())
                .where(FIREABLE_CONDITION, now)
                .orderBy(Job::getId).asc()
                .limit(limit));
    }

    /**
     * 抢占任务：只有仍处于待触发状态、且触发时间未变时才抢得到，抢不到说明已被其他节点领走
     *
     * @param jobId   任务id
     * @param nodeId  抢占的节点id
     * @param firedAt 抢占时间
     * @return 是否抢占成功
     */
    default boolean claim(Long jobId, String nodeId, LocalDateTime firedAt) {
        Job update = new Job();
        update.setStatus(JobStatus.FIRING.value());
        update.setFiredBy(nodeId);
        update.setFiredAt(firedAt);
        // 取件与抢占的条件必须完全一致，否则会出现「挑到了却抢不到」
        return this.updateByQuery(update, QueryWrapper.create()
                .where("id = ? and status = ? and (" + FIREABLE_CONDITION + ")",
                        jobId, JobStatus.WAITING.value(), firedAt)) > 0;
    }

    /**
     * 回写一次触发的结果：更新上/下次触发时间并把状态置回
     * <br>fired_at不再清空：下次抢占会整体覆盖，留着还方便排查上次是谁抢的
     *
     * @param jobId         任务id
     * @param prevFireTime  本次触发时间
     * @param nextFireTime  下次触发时间，为null表示不再触发（空cron算出不来）
     * @param status        回写后的状态
     * @return 更新的行数
     */
    default int finishFire(Long jobId, LocalDateTime prevFireTime, LocalDateTime nextFireTime, String status) {
        Job update = new Job();
        update.setPrevFireTime(prevFireTime);
        update.setNextFireTime(nextFireTime);
        update.setStatus(status);
        // 只回写仍处于执行中的任务：执行期间被停掉的任务状态已变，不能被改回待触发
        return this.updateByQuery(update, QueryWrapper.create()
                .eq(Job::getId, jobId)
                .eq(Job::getStatus, JobStatus.FIRING.value()));
    }

    /**
     * 查询抢占后迟迟没有回写的任务，节点中途宕机时会留下这种记录
     *
     * @param firedBefore 抢占时间早于该值的才算超时，为null表示不限（用于接管已下线节点的任务）
     * @return 待回收的任务
     */
    default List<Job> listStaleFired(LocalDateTime firedBefore) {
        QueryWrapper wrapper = QueryWrapper.create().eq(Job::getStatus, JobStatus.FIRING.value());
        if (firedBefore != null) {
            wrapper.lt(Job::getFiredAt, firedBefore);
        }
        return this.selectListByQuery(wrapper);
    }

    /**
     * 更新状态与下次触发时间
     * <br>写原生sql是因为停止时要把next_fire_time置空：按实体更新会跳过null字段，清不掉
     *
     * @param id           任务id
     * @param status       目标状态
     * @param nextFireTime 下次触发时间，停止时传null
     * @return 更新的行数
     */
    @Update("update sys_job set status = #{status}, next_fire_time = #{nextFireTime}, updated_at = now() where id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status,
                     @Param("nextFireTime") LocalDateTime nextFireTime);

    /**
     * 按查询参数拼查询条件
     *
     * @param query 查询参数
     * @return 查询条件
     */
    default QueryWrapper buildQueryWrapper(JobQuery query) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (query == null) {
            return wrapper;
        }
        if (query.getJobName() != null && !query.getJobName().isBlank()) {
            wrapper.like(Job::getJobName, query.getJobName());
        }
        if (query.getJobGroup() != null && !query.getJobGroup().isBlank()) {
            wrapper.eq(Job::getJobGroup, query.getJobGroup());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Job::getStatus, query.getStatus());
        }
        return wrapper;
    }

    /**
     * 查询分页
     *
     * @param pageQuery 分页与查询参数
     * @param wrapper   查询条件
     * @return 分页数据
     */
    default PageData<Job> selectPage(PageQuery<JobQuery> pageQuery, QueryWrapper wrapper) {
        wrapper.orderBy(DEFAULT_ORDER_BY);
        com.mybatisflex.core.paginate.Page<Job> page =
                this.paginate(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getTotal(), wrapper);
        return new PageData<>(page.getRecords(), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }
}
