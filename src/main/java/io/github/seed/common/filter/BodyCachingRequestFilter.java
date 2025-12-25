package io.github.seed.common.filter;

import io.github.seed.common.component.CachedBodyHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 2023/5/12 重复读取request body过滤器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class BodyCachingRequestFilter extends OncePerRequestFilter implements OrderedFilter {

    // 排除的路径url
    private static final String[] EXCLUDE_PATHS = new String[]{"/favicon.ico", "/actuator/**", "/swagger-ui/**", "/v3/api-docs"};
    // 最大允许缓存的大小8MB
    private static final long MAX_LENGTH = 8 * 1024L * 1024L;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        // 排除指定url
        boolean isExcludeUrl = PatternMatchUtils.simpleMatch(EXCLUDE_PATHS, uri);
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
        // 排除不带body的请求
        String method = request.getMethod();
        boolean isExcludeMethod = HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method) || HttpMethod.OPTIONS.matches(method) || HttpMethod.TRACE.matches(method);
        if (isExcludeMethod) {
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

        log.debug("BodyCachingRequestFilter in: {}", uri);
        // 包装request缓存body
        filterChain.doFilter(new CachedBodyHttpServletRequestWrapper(request), response);
    }

    @Override
    public int getOrder() {
        // 高执行顺序，只比CharacterEncodingFilter小
        return Ordered.HIGHEST_PRECEDENCE + 2;
    }

}
