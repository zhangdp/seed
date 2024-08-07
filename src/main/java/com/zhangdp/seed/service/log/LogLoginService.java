package com.zhangdp.seed.service.log;

import com.zhangdp.seed.entity.log.LogLogin;

/**
 * 2023/4/17 登录日志service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface LogLoginService {

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(LogLogin entity);
}
