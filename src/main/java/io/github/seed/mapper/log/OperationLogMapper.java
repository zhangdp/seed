package io.github.seed.mapper.log;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.seed.common.component.MybatisPlusHelper;
import io.github.seed.entity.log.OperationLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.OperationLogQuery;
import io.github.seed.model.params.PageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;

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
    default PageData<OperationLog>  selectPage(PageQuery<OperationLogQuery> pageQuery) {
        Page<OperationLog> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        page.setOrders(MybatisPlusHelper.toOrderItems(pageQuery.getOrderBy(), "operation_time DESC"));
        LambdaQueryWrapper<OperationLog> wrapper = Wrappers.lambdaQuery(OperationLog.class);
        OperationLogQuery params = pageQuery.getParams();
        if (params != null) {
            if (params.getUserId() != null) {
                wrapper.eq(OperationLog::getUserId, params.getUserId());
            }
            if (StrUtil.isNotBlank(params.getStartTime())) {
                wrapper.ge(OperationLog::getOperateTime, params.getStartTime());
            }
            if (StrUtil.isNotBlank(params.getEndTime())) {
                wrapper.lt(OperationLog::getOperateTime, params.getEndTime());
            }
            if (StrUtil.isNotBlank(params.getUri())) {
                wrapper.likeLeft(OperationLog::getUri, params.getUri());
            }
            if (StrUtil.isNotBlank(params.getRefModule())) {
                wrapper.eq(OperationLog::getRefModule, params.getRefModule().toLowerCase());
            }
            if (StrUtil.isNotBlank(params.getType())) {
                wrapper.eq(OperationLog::getType, params.getType().toUpperCase());
            }
            if (StrUtil.isNotBlank(params.getQuery())) {
                wrapper.eq(OperationLog::getTitle, params.getQuery());
            }
        }
        List<OperationLog> list = this.selectList(page, wrapper);
        return new PageData<>(list, page.getTotal(), page.getPages(), page.getSize());
    }
}
