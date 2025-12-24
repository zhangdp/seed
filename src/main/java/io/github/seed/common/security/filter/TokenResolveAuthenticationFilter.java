package io.github.seed.common.security.filter;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.core.text.dfa.SensitiveUtil;
import io.github.seed.common.enums.SensitiveType;
import io.github.seed.common.security.SecurityConst;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.data.AccessToken;
import io.github.seed.common.security.service.TokenService;
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
import org.springframework.security.core.userdetails.UserDetails;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        // 跳过放行的url
        if (!PatternMatchUtils.simpleMatch(SecurityConst.PERMIT_URLS, uri)) {
            String token = SecurityUtils.resolveToken(request);
            log.debug("TokenAuthenticationFilter: {}, token: {}", uri, SensitiveType.TOKEN.getDesensitizer().apply(token));
            if (StrUtil.isNotBlank(token)) {
                AccessToken accessToken = tokenService.loadAccessToken(token);
                if (accessToken != null) {
                    UserDetails userDetails = accessToken.getUserDetails();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContext context = SecurityContextHolder.getContext();
                    context.setAuthentication(authentication);
                    request.setAttribute(SecurityConst.REQUEST_ATTR_ACCESS_TOKEN, accessToken);
                    // 重置token过期时间
                    // tokenService.resetTokenExpireIfNecessary(token, userDetails);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
