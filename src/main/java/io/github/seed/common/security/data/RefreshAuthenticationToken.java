package io.github.seed.common.security.data;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collections;

/**
 * 2025/12/24 续签登录spring security对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
public class RefreshAuthenticationToken extends AbstractAuthenticationToken {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 认证前：refreshToken；认证成功后：userDetails
     */
    private final Object principal;

    public RefreshAuthenticationToken(Object principal) {
        super(Collections.emptyList());
        this.principal = principal;
    }

    public RefreshAuthenticationToken(UserDetails user) {
        super(user.getAuthorities());
        this.principal = user;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
