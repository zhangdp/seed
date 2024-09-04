package io.github.seed.service.sys.impl;

import io.github.seed.entity.sys.SysDict;
import io.github.seed.mapper.sys.SysDictMapper;
import io.github.seed.service.sys.SysDictService;
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
