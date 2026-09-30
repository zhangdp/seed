package io.github.seed.module.security.filter;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.enums.SensitiveType;
import io.github.seed.module.security.data.SecurityConst;
import io.github.seed.module.security.component.SecurityUtils;
import io.github.seed.module.security.data.LoginUser;
import io.github.seed.module.security.component.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
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
public class TokenResolveAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final String[] ignoreUrls;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        // 跳过放行的url
        if (ignoreUrls != null && ignoreUrls.length > 0 && !PatternMatchUtils.simpleMatch(ignoreUrls, uri)) {
            String token = SecurityUtils.resolveBearerToken(request);
            log.debug("TokenAuthenticationFilter: {}, token: {}", uri, SensitiveType.TOKEN.getDesensitizer().apply(token));
            if (StrUtil.isNotBlank(token)) {
                // jwt自包含：验签与黑名单都在本地完成，不查redis
                LoginUser userDetails = tokenService.loadLoginUser(token);
                if (userDetails != null) {
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContext context = SecurityContextHolder.getContext();
                    context.setAuthentication(authentication);
                    request.setAttribute(SecurityConst.REQUEST_ATTR_ACCESS_TOKEN, token);
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}

