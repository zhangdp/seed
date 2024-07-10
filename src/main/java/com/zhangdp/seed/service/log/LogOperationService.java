package com.zhangdp.seed.service.log;

import com.zhangdp.seed.entity.log.LogOperation;

/**
 * 2023/4/17 操作日志service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface LogOperationService {

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(LogOperation entity);

}
