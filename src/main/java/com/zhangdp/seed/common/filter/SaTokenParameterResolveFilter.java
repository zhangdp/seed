package com.zhangdp.seed.common.filter;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 2023/6/9 sa-token从参数解析出token
 *
 * @author zhangdp
 * @since
 */
@Slf4j
@Order(-101)
@RequiredArgsConstructor
public class SaTokenParameterResolveFilter extends OncePerRequestFilter {

    /**
     * sa-token配置
     */
    private final SaTokenConfig saTokenConfig;

    /**
     * 解析token的参数名称
     */
    @Getter
    @Setter
    @Value("${sa-token.parameter-token-name:}")
    private String parameterTokenName;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("SaTokenParameterResolveFilter in: {}", request.getRequestURI());
        }
        if (StrUtil.isNotBlank(parameterTokenName)) {
            String headerName = saTokenConfig.getTokenName();
            if (StrUtil.isBlank(request.getHeader(headerName))) {
                String token = request.getParameter(parameterTokenName);
                if (StrUtil.isNotBlank(token)) {
                    StpUtil.getStpLogic().setTokenValueToStorage(token);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
