package io.github.seed.module.security.data;

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

    /**
     * 按角色编码生成角色授权标识，统一前缀与大小写，缓存的key也用它
     *
     * @param roleCode 角色编码，为空时返回null
     * @return 授权标识，形如ROLE_ADMIN
     */
    public static String roleAuthority(String roleCode) {
        if (roleCode == null || (roleCode = roleCode.trim()).isEmpty()) {
            return null;
        }
        String code = roleCode.toUpperCase();
        return code.startsWith(SecurityConst.ROLE_PREFIX) ? code : SecurityConst.ROLE_PREFIX + code;
    }

    /**
     * 按权限编码生成权限授权标识，统一大写
     *
     * @param permissionCode 权限编码，为空时返回null
     * @return 授权标识，形如SYS:USER:READ
     */
    public static String permissionAuthority(String permissionCode) {
        if (permissionCode == null || (permissionCode = permissionCode.trim()).isEmpty()) {
            return null;
        }
        return permissionCode.toUpperCase();
    }

}

