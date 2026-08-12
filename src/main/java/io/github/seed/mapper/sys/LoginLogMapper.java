package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.LoginLog;
import io.github.seed.model.PageData;
import io.github.seed.model.query.CursorPageQuery;
import io.github.seed.model.query.LoginLogQuery;
import io.github.seed.model.query.PageQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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

    /**
     * 游标分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<LoginLog> cursorSelectPage(CursorPageQuery<LoginLogQuery> pageQuery) {
        LoginLogQuery params = pageQuery.getParams();
        QueryWrapper wrapper = QueryWrapper.create();
        if (params != null) {
            wrapper.eq(LoginLog::getUserId, params.getUserId())
                    .eq(LoginLog::getType, params.getLoginType())
                    .lt(LoginLog::getLoginAt, params.getEndTime())
                    .gt(LoginLog::getLoginAt, params.getStartTime());
        }
        long total = -1;
        if (pageQuery.isCountTotal()) {
            total = this.selectCountByQuery(wrapper);
        }
        List<LoginLog> list = null;
        if (total == -1 || total > 0) {
            if (pageQuery.getCursor() != null) {
                if (pageQuery.isDesc()) {
                    wrapper.lt(LoginLog::getId, pageQuery.getCursor());
                } else {
                    wrapper.gt(LoginLog::getId, pageQuery.getCursor());
                }
            }
            wrapper.orderBy(LoginLog::getId, !pageQuery.isDesc()).limit(pageQuery.getSize());
            list = this.selectListByQuery(wrapper);
        }
        return new PageData<>(list, pageQuery.getPage(), pageQuery.getSize(), total);
    }
}
