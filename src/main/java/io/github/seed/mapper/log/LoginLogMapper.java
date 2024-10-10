package io.github.seed.mapper.log;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.seed.common.util.MybatisPlusHelper;
import io.github.seed.entity.log.LoginLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.LoginLogQuery;
import io.github.seed.model.params.PageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.hutool.core.text.StrUtil;

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
        Page<LoginLog> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        page.setOrders(MybatisPlusHelper.toOrderItems(pageQuery.getOrderBy(), "operation_time DESC"));
        LambdaQueryWrapper<LoginLog> wrapper = Wrappers.lambdaQuery(LoginLog.class);
        LoginLogQuery params = pageQuery.getParams();
        if (params != null) {
            if (params.getUserId() != null) {
                wrapper.eq(LoginLog::getUserId, params.getUserId());
            }
            if (StrUtil.isNotBlank(params.getStartTime())) {
                wrapper.ge(LoginLog::getLoginTime, params.getStartTime());
            }
            if (StrUtil.isNotBlank(params.getEndTime())) {
                wrapper.lt(LoginLog::getLoginTime, params.getEndTime());
            }
            if (StrUtil.isNotBlank(params.getLoginType())) {
                wrapper.likeLeft(LoginLog::getLoginType, params.getLoginType());
            }
        }
        List<LoginLog> list = this.selectList(page, wrapper);
        return new PageData<>(list, page.getTotal(), page.getPages(), page.getSize());
    }

}
