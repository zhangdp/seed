package io.github.seed.common.security.filter;

import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("TokenAuthenticationFilter: {}", request.getRequestURI());
        String token = SecurityUtils.resolveToken(request);
        if (StrUtil.isNotBlank(token)) {
            UserDetails userDetails = tokenService.loadPrincipal(token);
            if (userDetails != null) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // 重置token过期时间
                tokenService.resetTokenExpireIn(token);
            }
        }
        filterChain.doFilter(request, response);
    }

}
