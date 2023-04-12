package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.entity.sys.SysParam;
import com.zhangdp.seed.mapper.sys.SysParamMapper;
import com.zhangdp.seed.service.sys.SysParamService;
import org.springframework.stereotype.Service;

/**
 * 2023/4/12 系统参数service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class SysParamServiceImpl extends ServiceImpl<SysParamMapper, SysParam> implements SysParamService {

    @Override
    public SysParam getByCode(String code) {
        return this.getOne(Wrappers.lambdaQuery(SysParam.class).eq(SysParam::getCode, code));
    }
}
