package io.github.seed.common.security.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

/**
 * 自定义的spring security授权对象，可区分角色、权限并可包含id方便需要的时候取用
 *
 * @author zhangdp
 * @since 2024/6/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionGrantedAuthority implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 权限标识符
     */
    private String authority;
    /**
     * 权限类型
     */
    private AuthorityType type;
    /**
     * id
     */
    private Long id;

    /**
     * 权限类型枚举
     */
    public enum AuthorityType {
        ROLE, PERMISSION
    }

}

