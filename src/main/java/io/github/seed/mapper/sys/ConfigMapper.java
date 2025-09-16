package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.log.OperationLog;
import io.github.seed.entity.sys.Config;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.OperationLogQuery;
import io.github.seed.model.params.PageQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/12 系统配置mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {

    /**
     * 根据key查询单条记录
     *
     * @param configKey
     * @return
     */
    default Config selectOneByConfigKey(String configKey) {
        return this.selectOneByQuery(QueryWrapper.create().select().eq(Config::getConfigKey, configKey));
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<Config> queryPage(PageQuery<BaseQueryParams> pageQuery) {
        BaseQueryParams params = pageQuery.getParams();
        QueryWrapper wrapper = QueryWrapper.create().orderBy(pageQuery.getOrderBy());
        if (params != null) {
            wrapper.like(Config::getConfigKey, params.getQuery());
        }
        Page<Config> page = this.paginate(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getTotal(), wrapper);
        return new PageData<>(page.getRecords(), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }

}
