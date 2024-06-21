package com.zhangdp.seed.common.security;

import com.zhangdp.seed.common.constant.SecurityConst;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * spring security解析jwt token过滤器
 *
 * @author zhangdp
 * @since 2024/1/5
 */
@Slf4j
// @Component
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 从header中获取token
        String token = request.getHeader(SecurityConst.AUTHORIZATION_HEADER);
        if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConst.AUTH_TYPE_BEARER)) {
            token = token.substring(SecurityConst.AUTH_TYPE_BEARER.length());
        }
        // 如果header中没有token，则从请求参数中获取
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(SecurityConst.AUTHORIZATION_PARAMETER);
        }
        filterChain.doFilter(request, response);
    }
}
