package io.github.seed.module.security.handler;

import io.github.seed.module.security.data.SmsAuthenticationToken;
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
        String mobile = (String) smsAuthenticationToken.getPrincipal();
        String code = smsAuthenticationToken.getCode();
        // 防御性判空：正常情况下验证码已由登录接口上的@VerifyCaptcha切面校验并消费
        if (code == null || (code = code.trim()).isEmpty()) {
            throw new BadCredentialsException("短信验证码错误");
        }
        // 注意：短信验证码的校验与消费在controller方法上的@VerifyCaptcha切面中完成，此处不可重复校验，
        // 否则验证码已被切面删除，重新读取必然失败；本Provider只负责手机号到用户的映射
        UserDetails user = userDetailsService.loadUserByUsername(mobile);
        return new SmsAuthenticationToken(user);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
