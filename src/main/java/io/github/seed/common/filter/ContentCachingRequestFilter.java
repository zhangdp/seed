package io.github.seed.common.filter;

import cn.hutool.v7.http.server.servlet.ServletUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
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

    // 最大允许缓存的大小10MB
    private static final long MAX_LENGTH = 10 * 1024 * 1024;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        log.trace("ContentCachingRequestFilter in:{}", request.getRequestURI());
        HttpServletRequest req = request;
        // 不缓存上传文件请求防止过大内存溢出
        if (!ServletUtil.isMultipart(request) && req.getContentLengthLong() <= MAX_LENGTH) {
            req = new ContentCachingRequestWrapper(request);
        }
        filterChain.doFilter(req, response);
    }
}
