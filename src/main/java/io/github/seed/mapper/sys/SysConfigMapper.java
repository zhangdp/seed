package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.seed.common.util.MybatisPlusHelper;
import io.github.seed.entity.sys.SysConfig;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.hutool.core.text.StrUtil;

import java.util.List;

/**
 * 2023/4/12 系统配置mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据key查询单条记录
     *
     * @param configKey
     * @return
     */
    default SysConfig selectOneByConfigKey(String configKey) {
        return this.selectOne(Wrappers.lambdaQuery(SysConfig.class).eq(SysConfig::getConfigKey, configKey));
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<SysConfig> queryPage(PageQuery<BaseQueryParams> pageQuery) {
        LambdaQueryWrapper<SysConfig> wrappers = Wrappers.lambdaQuery(SysConfig.class);
        BaseQueryParams param = pageQuery.getParams();
        if (param != null) {
            if (StrUtil.isNotBlank(param.getQuery())) {
                String query = param.getQuery().trim().toUpperCase();
                wrappers.like(SysConfig::getConfigKey, query)
                        .or()
                        .like(SysConfig::getDescription, query);
            }
        }
        Page<SysConfig> page = new Page<>(pageQuery.getPage(), pageQuery.getSize());
        page.setOrders(MybatisPlusHelper.toOrderItems(pageQuery.getOrderBy(), "id"));
        List<SysConfig> list = this.selectList(page, wrappers);
        return new PageData<>(list, page.getTotal(), page.getPages(), page.getSize());
    }
}
