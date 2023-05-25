package com.zhangdp.seed.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.http.server.servlet.JakartaServletUtil;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 2023/5/12 重复读取request body过滤器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@Deprecated
public class ContentCachingRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (log.isTraceEnabled()) {
            log.trace("ContentCachingRequestFilter in:{}", request.getRequestURI());
        }
        // get请求没有body；不缓存文件防止过大内存溢出
        if (!"GET".equalsIgnoreCase(request.getMethod()) && !JakartaServletUtil.isMultipart(request)) {
            ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
            filterChain.doFilter(wrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
