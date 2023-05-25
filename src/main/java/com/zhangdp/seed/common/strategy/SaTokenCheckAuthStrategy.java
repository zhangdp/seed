package com.zhangdp.seed.common.strategy;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 2023/5/25 sa-token 认证检查策略
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SaTokenCheckAuthStrategy {

    /**
     * 执行
     *
     * @param request
     */
    void run(HttpServletRequest request);

}
