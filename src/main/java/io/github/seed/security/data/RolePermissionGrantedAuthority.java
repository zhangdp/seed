package io.github.seed.security.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

/**
 * 角色、权限
 *
 * @author zhangdp
 * @since 2024/6/26
 */
@Data
@Accessors(chain = true)
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

