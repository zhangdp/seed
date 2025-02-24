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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * spring security解析token过滤器
 *
 * @author zhangdp
 * @since 2024/1/5
 */
@Slf4j
// @Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final RequestAttributeSecurityContextRepository repository = new RequestAttributeSecurityContextRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.trace("TokenAuthenticationFilter: {}", request.getRequestURI());
        String token = SecurityUtils.resolveToken(request);
        if (StrUtil.isNotBlank(token)) {
            UserDetails userDetails = tokenService.loadUserDetails(token);
            if (userDetails != null) {
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);
                // 保存context用于sse等异步
                repository.saveContext(context, request, response);
                // 重置token过期时间
                tokenService.resetTokenExpireIfNecessary(token, userDetails);
            }
        }
        filterChain.doFilter(request, response);
    }

}
