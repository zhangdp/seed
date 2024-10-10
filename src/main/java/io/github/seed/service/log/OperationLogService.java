package io.github.seed.service.log;

import io.github.seed.entity.log.OperationLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.OperationLogQuery;
import io.github.seed.model.params.PageQuery;
import jakarta.validation.Valid;

/**
 * 2023/4/17 操作日志service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface OperationLogService {

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(OperationLog entity);

    /**
     * 分页查询操作日志
     *
     * @param pageQuery
     * @return
     */
    PageData<OperationLog> queryPage(PageQuery<OperationLogQuery> pageQuery);
}
