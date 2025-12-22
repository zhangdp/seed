package io.github.seed.common.security.data;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * 2025/12/17 短信验证码登录对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public class SmsAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * 手机号
     */
    private String mobile;
    /**
     * 验证码
     */
    private String code;

    public SmsAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public SmsAuthenticationToken(String mobile, String code) {
        super(null);
        this.mobile = mobile;
        this.code = code;
    }

    public SmsAuthenticationToken(UserDetails user) {
        super(user.getAuthorities());
        setAuthenticated(true);
        setDetails(user);
    }

    @Override
    public Object getCredentials() {
        return this.getMobile();
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
