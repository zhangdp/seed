package io.github.seed.service.log.impl;

import io.github.seed.entity.log.LogOperation;
import io.github.seed.mapper.log.LogOperationMapper;
import io.github.seed.service.log.LogOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2023/4/17 操作日志service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class LogOperationServiceImpl implements LogOperationService {

    private final LogOperationMapper logOperationMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(LogOperation entity) {
        return logOperationMapper.insert(entity) > 0;
    }
}
