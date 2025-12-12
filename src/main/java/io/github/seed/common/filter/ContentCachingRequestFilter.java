package io.github.seed.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Set;

/**
 * 2023/5/12 重复读取request body过滤器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class ContentCachingRequestFilter extends OncePerRequestFilter implements OrderedFilter {

    // 排除的路径url
    private static final Set<String> EXCLUDE_PATHS = Set.of("/actuator/**", "/swagger-ui/**", "/v3/api-docs");
    // 最大允许缓存的大小1MB
    private static final long MAX_LENGTH = 1024L * 1024L;

    // 路径匹配器
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        log.debug("ContentCachingRequestFilter in: {}", uri);
        // 排除指定url
        boolean isExcludeUrl = !EXCLUDE_PATHS.isEmpty() && EXCLUDE_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
        if (isExcludeUrl) {
            filterChain.doFilter(request, response);
            return;
        }
        // 不缓存上传文件请求防止过大内存溢出
        String contentType = request.getContentType();
        boolean isMultipart = contentType != null && contentType.trim().toLowerCase().startsWith("multipart/");
        if (isMultipart) {
            filterChain.doFilter(request, response);
            return;
        }
        // 不缓存GET、HEAD请求
        String method = request.getMethod();
        boolean isGetOrHeadMethod = "GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method);
        if (isGetOrHeadMethod) {
            filterChain.doFilter(request, response);
            return;
        }
        // 不缓存超大请求，-1的时候是chunked
        long len = request.getContentLengthLong();
        boolean isNotAllowLength = len <= 0 || len > MAX_LENGTH;
        if (isNotAllowLength) {
            filterChain.doFilter(request, response);
            return;
        }
        // 使用ContentCachingRequestWrapper缓存request body
        filterChain.doFilter(new ContentCachingRequestWrapper(request), response);
    }

    @Override
    public int getOrder() {
        // 最高执行顺序，只比全局异常处理小1
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }
}
