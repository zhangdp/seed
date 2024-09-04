package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
        PageHelper.startPage(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getOrderBy());
        LambdaQueryWrapper<SysConfig> wrappers = Wrappers.lambdaQuery(SysConfig.class).orderByDesc(SysConfig::getId);
        BaseQueryParams param = pageQuery.getParams();
        if (param != null) {
            if (StrUtil.isNotBlank(param.getQuery())) {
                wrappers.like(SysConfig::getConfigKey, param.getQuery())
                        .or()
                        .like(SysConfig::getDescription, param.getQuery());
            }
        }
        List<SysConfig> list = this.selectList(wrappers);
        PageInfo<SysConfig> pi = new PageInfo<>(list);
        return new PageData<>(list, pi.getTotal(), pi.getPageNum(), pi.getPageSize());
    }
}
