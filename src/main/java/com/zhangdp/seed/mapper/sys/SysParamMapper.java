package com.zhangdp.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangdp.seed.entity.sys.SysParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/12 系统参数mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysParamMapper extends BaseMapper<SysParam> {

    /**
     * 根据code查询单条记录
     *
     * @param code
     * @return
     */
    default SysParam selectOneByCode(String code) {
        return this.selectOne(Wrappers.lambdaQuery(SysParam.class).eq(SysParam::getCode, code));
    }
}
