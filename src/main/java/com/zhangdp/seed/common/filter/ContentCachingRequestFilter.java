package com.zhangdp.seed.common.filter;

import com.zhangdp.seed.common.util.WebUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 2023/5/12 重复读取request body过滤器
 *
 * @author zhangdp
 * @since
 */
@Slf4j
// @Component
// @Order(Ordered.HIGHEST_PRECEDENCE)
@Deprecated
public class ContentCachingRequestFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("ContentCachingRequestFilter in:{}", request.getRequestURI());
        }
        // get请求没有body；不缓存文件防止过大内存溢出
        if (!"GET".equalsIgnoreCase(request.getMethod()) && !WebUtils.isMultipart(request)) {
            ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(request);
            filterChain.doFilter(wrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
