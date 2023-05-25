package com.zhangdp.seed.common.strategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 2023/5/25 sa-token 认证检查失败策略
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SaTokenCheckAuthErrorStrategy {

    /**
     * 执行
     *
     * @param request
     * @param response
     * @param t
     */
    void run(HttpServletRequest request, HttpServletResponse response, Throwable t) throws IOException;

}
