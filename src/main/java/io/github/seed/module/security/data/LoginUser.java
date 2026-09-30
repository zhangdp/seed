package io.github.seed.module.security.data;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 登录用户信息
 * <br>由jwt载荷还原，因此只带鉴权必需字段：手机号、邮箱、姓名、头像等档案信息
 * 走{@code /auth/user/info}按需查，不放进令牌也不放进这里
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Hidden
public final class LoginUser implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long id;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     * <br>从令牌还原时为空：已认证的身份不需要再校验密码
     */
    private String password;
    /**
     * 拥有的角色权限列表
     */
    private List<RolePermissionGrantedAuthority> authorities;
    /**
     * 账号是否未过期，默认true
     */
    private boolean accountNonExpired = true;
    /**
     * 密码是否未过期，默认ture
     */
    private boolean credentialsNonExpired = true;
    /**
     * 账号是否未锁定，默认true
     */
    private boolean accountNonLocked = true;
    /**
     * 账号是否可用，默认ture
     */
    private boolean enabled = true;

    /**
     * 简易字符串
     *
     * @return
     */
    public String simpleString() {
        return "[" + this.id + "]" + this.username;
    }
}
