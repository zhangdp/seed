package io.github.seed.service.log.impl;

import io.github.seed.entity.log.OperationLog;
import io.github.seed.mapper.log.OperationLogMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.params.OperationLogQuery;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.log.OperationLogService;
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
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(OperationLog entity) {
        return operationLogMapper.insert(entity) > 0;
    }

    @Override
    public PageData<OperationLog> queryPage(PageQuery<OperationLogQuery> pageQuery) {
        return operationLogMapper.selectPage(pageQuery);
    }
}
