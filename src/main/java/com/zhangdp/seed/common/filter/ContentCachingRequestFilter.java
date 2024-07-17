package com.zhangdp.seed.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.http.server.servlet.ServletUtil;
import org.springframework.stereotype.Component;
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
@Component
public class ContentCachingRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("ContentCachingRequestFilter in:{}", request.getRequestURI());
        }
        HttpServletRequest req = request;
        // get请求没有body；不缓存文件防止过大内存溢出
        if (!"GET".equalsIgnoreCase(request.getMethod()) && !ServletUtil.isMultipart(request)) {
            req = new ContentCachingRequestWrapper(request);
        }
        filterChain.doFilter(req, response);

    }
}
