package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.entity.sys.SysDict;
import com.zhangdp.seed.mapper.sys.SysDictMapper;
import com.zhangdp.seed.service.sys.SysDictService;
import org.springframework.stereotype.Service;

/**
 * 2023/4/12 字典service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Override
    public SysDict getByType(String type) {
        return this.getOne(Wrappers.lambdaQuery(SysDict.class).eq(SysDict::getType, type));
    }
}
