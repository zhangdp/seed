package io.github.seed.service.sys.impl;

import io.github.seed.entity.sys.Dict;
import io.github.seed.mapper.sys.DictMapper;
import io.github.seed.service.sys.DictService;
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
public class DictServiceImpl implements DictService {

    private final DictMapper dictMapper;

    @Override
    public Dict getByType(String type) {
        return dictMapper.selectOneByType(type);
    }
}
