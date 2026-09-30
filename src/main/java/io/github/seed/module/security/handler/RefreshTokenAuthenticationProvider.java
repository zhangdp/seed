package io.github.seed.module.security.handler;

import io.github.seed.module.security.data.RefreshAuthenticationToken;
import io.github.seed.module.security.data.RefreshToken;
import io.github.seed.module.security.component.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 2025/12/24 刷新令牌登录处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getPrincipal();
        RefreshToken refreshToken = tokenService.loadRefreshToken(token);
        if (refreshToken == null) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        // 相当于全新登录，重新查库拿最新角色，签发新的访问令牌、刷新令牌
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUsername());
        // 必须回RefreshAuthenticationToken：登录类型按认证对象的子类型推断，返回成短信登录对象会记错日志
        RefreshAuthenticationToken result = new RefreshAuthenticationToken(userDetails);
        // 旧令牌失效：访问令牌按jti拉黑，刷新令牌一并作废，防止旧刷新令牌被重复使用
        tokenService.removeToken(refreshToken);
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
