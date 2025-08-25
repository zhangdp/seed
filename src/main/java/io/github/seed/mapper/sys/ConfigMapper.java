package io.github.seed.mapper.sys;

import cn.hutool.v7.core.text.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.seed.common.component.MybatisPlusHelper;
import io.github.seed.entity.sys.Config;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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
        return this.selectOne(Wrappers.lambdaQuery(Config.class).eq(Config::getConfigKey, configKey));
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<Config> queryPage(PageQuery<BaseQueryParams> pageQuery) {
        LambdaQueryWrapper<Config> wrappers = Wrappers.lambdaQuery(Config.class);
        BaseQueryParams param = pageQuery.getParams();
        if (param != null) {
            if (StrUtil.isNotBlank(param.getQuery())) {
                String query = param.getQuery().trim().toUpperCase();
                wrappers.like(Config::getConfigKey, query)
                        .or()
                        .like(Config::getDescription, query);
            }
        }
        Page<Config> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        page.setOrders(MybatisPlusHelper.toOrderItems(pageQuery.getOrderBy(), "id"));
        List<Config> list = this.selectList(page, wrappers);
        return new PageData<>(list, page.getTotal(), page.getPages(), page.getSize());
    }

    /**
     * 查询列表
     *
     * @return
     */
    default List<Config> selectList() {
        return this.selectList(Wrappers.lambdaQuery(Config.class).orderByAsc(Config::getId));
    }
}
