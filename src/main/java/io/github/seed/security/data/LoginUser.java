package io.github.seed.security.data;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 登录用户信息
 *
 * @author zhangdp
 * @since 2024/1/8
 */
@Data
@Accessors(chain = true)
@Hidden
public class LoginUser implements Serializable, UserDetails {

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
     */
    private String password;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 姓名
     */
    private String name;
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

}
