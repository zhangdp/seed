package io.github.seed.common.security.handler;

import io.github.seed.common.security.data.RefreshAuthenticationToken;
import io.github.seed.common.security.data.RefreshToken;
import io.github.seed.common.security.data.SmsAuthenticationToken;
import io.github.seed.common.security.service.TokenService;
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
        // 相当于全新登录，签发新的访问令牌、刷新令牌
        UserDetails userDetails = userDetailsService.loadUserByUsername(refreshToken.getUsername());
        SmsAuthenticationToken result = new SmsAuthenticationToken(userDetails);
        // 清理掉旧的令牌
        tokenService.removeToken(refreshToken.getAccessToken());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
