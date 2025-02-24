package io.github.seed.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * 2024/7/4 异常过滤器，捕获所有filter产生的异常，委托给spring 全局异常处理器处理，从而响应特定格式内容
 * </br>最高执行顺序，在所有filter之前
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@RequiredArgsConstructor
@Slf4j
public class ExceptionResolverFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.trace("ExceptionResolverFilter: {}", request.getRequestURI());
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            // 委托给全局异常处理器
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
