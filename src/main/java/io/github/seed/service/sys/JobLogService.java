package io.github.seed.service.sys;

import io.github.seed.entity.sys.JobLog;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobLogQuery;
import io.github.seed.model.query.PageQuery;

/**
 * 定时任务执行日志服务
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface JobLogService {

    /**
     * 分页查询
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    PageData<JobLog> queryPage(PageQuery<JobLogQuery> pageQuery);

    /**
     * 保存一条执行日志，超长的结果描述会被截断
     *
     * @param jobLog 执行日志
     * @return 是否保存成功
     */
    boolean save(JobLog jobLog);

    /**
     * 清空某个任务的执行日志
     *
     * @param jobId 任务id
     * @return 删除的条数
     */
    int deleteByJobId(Long jobId);

    /**
     * 清空全部执行日志
     *
     * @return 删除的条数
     */
    int clear();
}
