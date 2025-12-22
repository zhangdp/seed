package io.github.seed.common.security.handler;

import io.github.seed.common.security.data.SmsAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 2025/12/17 短信验证码认证处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsAuthenticationToken smsAuthenticationToken = (SmsAuthenticationToken) authentication;
        String mobile = smsAuthenticationToken.getMobile();
        String code = smsAuthenticationToken.getCode();
        if (code == null || (code = code.trim()).isEmpty()) {
            throw new BadCredentialsException("短信验证码错误");
        }
        // todo 校验短信验证码
        UserDetails user = userDetailsService.loadUserByUsername(mobile);
        return new SmsAuthenticationToken(user);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
