package io.github.seed.mapper.log;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.log.LoginLog;
import io.github.seed.entity.log.OperationLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.LoginLogQuery;
import io.github.seed.model.params.OperationLogQuery;
import io.github.seed.model.params.PageQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/17 操作日志mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {

    /**
     * 查询分页
     *
     * @param pageQuery
     * @return
     */
    default PageData<OperationLog> selectPage(PageQuery<OperationLogQuery> pageQuery) {
        OperationLogQuery params = pageQuery.getParams();
        QueryWrapper wrapper = QueryWrapper.create().orderBy(pageQuery.getOrderBy());
        if (params != null) {
            wrapper.eq(OperationLog::getUserId, params.getUserId())
                    .eq(OperationLog::getRefModule, params.getRefModule())
                    .eq(OperationLog::getType, params.getType())
                    .lt(OperationLog::getOperateTime, params.getEndTime())
                    .gt(OperationLog::getOperateTime, params.getStartTime());
        }
        Page<OperationLog> page = this.paginate(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getTotal(), wrapper);
        return new PageData<>(page.getRecords(), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }
}
