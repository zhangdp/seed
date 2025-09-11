package io.github.seed.model.dto;

import io.github.seed.common.constant.Const;
import io.github.seed.entity.sys.Dept;
import io.github.seed.entity.sys.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 2023/5/17 用户信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Schema(title = "用户信息")
public class UserInfo extends User implements Serializable, UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 部门名称
     */
    @Schema(title = "部门")
    private Dept dept;
    /**
     * 角色列表
     */
    @Schema(title = "角色列表")
    private List<String> roles;
    /**
     * 权限列表
     */
    @Schema(title = "权限列表")
    private List<String> permissions;

    /**
     * 获取部门名称
     *
     * @return
     */
    @Schema(title = "部门名称")
    public String getDeptName() {
        return dept == null ? null : dept.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null && !roles.isEmpty()) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }
        if (permissions != null && !permissions.isEmpty()) {
            for (String permission : permissions) {
                authorities.add(new SimpleGrantedAuthority(permission));
            }
        }
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.getStatus() == Const.GOOD;
    }
}
