package com.zhangdp.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangdp.seed.entity.sys.SysProperties;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/12 系统参数mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysPropertiesMapper extends BaseMapper<SysProperties> {

    /**
     * 根据code查询单条记录
     *
     * @param code
     * @return
     */
    default SysProperties selectOneByCode(String code) {
        return this.selectOne(Wrappers.lambdaQuery(SysProperties.class).eq(SysProperties::getCode, code));
    }
}
