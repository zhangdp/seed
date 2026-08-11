package io.github.seed.service.sys.impl;

import io.github.seed.entity.sys.OperationLog;
import io.github.seed.mapper.sys.OperationLogMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.query.OperationLogTextQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.OperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteBatch(Collection<Long> ids) {
        return this.operationLogMapper.deleteBatchByIds(ids);
    }

    @Override
    public PageData<OperationLog> queryPage(PageQuery<OperationLogTextQuery> pageQuery) {
        return operationLogMapper.selectPage(pageQuery);
    }
}
