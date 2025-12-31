package io.github.seed.common.filter;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.ForbiddenException;
import io.github.seed.common.util.WebUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ip过滤器
 *
 * @author zhangdp
 * @since 2025/3/14
 */
@Slf4j
public class IpRequestFilter extends OncePerRequestFilter {

    /**
     * ip列表
     */
    private final Set<String> ipSet;
    /**
     * 过滤模式，默认白名单
     */
    private final FilterMode filterMode;

    public IpRequestFilter(Collection<String> ipList, FilterMode filterMode) {
        this.ipSet = new HashSet<>(ipList);
        this.filterMode = filterMode;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (log.isTraceEnabled()) {
            log.trace("IP Request Filter in. uri={}, ipSet={}, filterMode={}", request.getRequestURI(), ipSet, filterMode);
        }
        String ip = WebUtils.getClientIP(request);
        boolean flag = StrUtil.isNotBlank(ip) && ipSet.contains(ip);
        // ip不在白名单里面时不允许访问
        if (filterMode == FilterMode.WHITE_LIST && !flag) {
            throw new ForbiddenException(ErrorCode.IP_NOT_ALLOW);
        }
        // ip在黑名单里时不允许访问
        if (filterMode == FilterMode.BLACK_LIST && flag) {
            throw new ForbiddenException(ErrorCode.IP_NOT_ALLOW);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 过滤模式枚举
     */
    public enum FilterMode {
        // 黑名单
        WHITE_LIST,
        // 白名单
        BLACK_LIST
    }

}
