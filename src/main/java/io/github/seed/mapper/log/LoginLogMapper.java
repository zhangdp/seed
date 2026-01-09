package io.github.seed.mapper.log;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.log.LoginLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.LoginLogQuery;
import io.github.seed.model.params.PageQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/17 登录日志mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    /**
     * 查询分页
     *
     * @param pageQuery
     * @return
     */
    default PageData<LoginLog> selectPage(PageQuery<LoginLogQuery> pageQuery) {
        LoginLogQuery params = pageQuery.getParams();
        QueryWrapper wrapper = QueryWrapper.create().orderBy(pageQuery.getOrderBy());
        if (params != null) {
            wrapper.eq(LoginLog::getUserId, params.getUserId())
                    .eq(LoginLog::getType, params.getLoginType())
                    .lt(LoginLog::getLoginAt, params.getEndTime())
                    .gt(LoginLog::getLoginAt, params.getStartTime());
        }
        Page<LoginLog> page = this.paginate(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getTotal(), wrapper);
        return new PageData<>(page.getRecords(), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }

}
