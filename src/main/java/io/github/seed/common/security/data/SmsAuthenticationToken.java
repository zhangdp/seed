package io.github.seed.common.security.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collections;

/**
 * 2025/12/17 短信验证码登录对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 认证前：手机号；认证后：userDetails
     */
    private final Object principal;
    /**
     * 验证码
     */
    private String code;

    public SmsAuthenticationToken(Object principal, String code) {
        super(Collections.emptyList());
        this.principal = principal;
        this.code = code;
    }

    public SmsAuthenticationToken(UserDetails user) {
        super(user.getAuthorities());
        this.principal = user;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
