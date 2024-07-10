package com.zhangdp.seed.service.sys.impl;

import com.zhangdp.seed.entity.sys.SysDict;
import com.zhangdp.seed.mapper.sys.SysDictMapper;
import com.zhangdp.seed.service.sys.SysDictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 2023/4/12 字典service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl implements SysDictService {

    private final SysDictMapper sysDictMapper;

    @Override
    public SysDict getByType(String type) {
        return sysDictMapper.selectOneByType(type);
    }
}
