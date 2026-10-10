package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.JobLog;
import io.github.seed.model.PageData;
import io.github.seed.model.query.JobLogQuery;
import io.github.seed.model.query.PageQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务执行日志mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface JobLogMapper extends BaseMapper<JobLog> {

    /**
     * 分页查询未指定排序时的默认排序：新的在前
     */
    String DEFAULT_ORDER_BY = "id desc";

    /**
     * 查询分页
     *
     * @param pageQuery 分页与查询参数
     * @return 分页数据
     */
    default PageData<JobLog> selectPage(PageQuery<JobLogQuery> pageQuery) {
        QueryWrapper wrapper = this.buildQueryWrapper(pageQuery.getParams());
        wrapper.orderBy(DEFAULT_ORDER_BY);
        com.mybatisflex.core.paginate.Page<JobLog> page =
                this.paginate(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getTotal(), wrapper);
        return new PageData<>(page.getRecords(), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }

    /**
     * 按查询参数拼查询条件，时间范围按started_at过滤
     *
     * @param query 查询参数
     * @return 查询条件
     */
    default QueryWrapper buildQueryWrapper(JobLogQuery query) {
        QueryWrapper wrapper = QueryWrapper.create();
        if (query == null) {
            return wrapper;
        }
        if (query.getJobId() != null) {
            wrapper.eq(JobLog::getJobId, query.getJobId());
        }
        if (query.getJobName() != null && !query.getJobName().isBlank()) {
            wrapper.like(JobLog::getJobName, query.getJobName());
        }
        if (query.getStatus() != null) {
            wrapper.eq(JobLog::getStatus, query.getStatus());
        }
        if (query.getTriggerType() != null) {
            wrapper.eq(JobLog::getTriggerType, query.getTriggerType());
        }
        if (query.getStartTime() != null && !query.getStartTime().isBlank()) {
            wrapper.ge(JobLog::getStartedAt, query.getStartTime());
        }
        if (query.getEndTime() != null && !query.getEndTime().isBlank()) {
            wrapper.le(JobLog::getStartedAt, query.getEndTime());
        }
        return wrapper;
    }
}
