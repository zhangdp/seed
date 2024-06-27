package com.zhangdp.seed.common.security;

import lombok.AllArgsConstructor;
import lombok.Data;
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
@AllArgsConstructor
public class RolePermissionGrantedAuthority implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 权限标识符
     */
    private String code;
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

    @Override
    public String getAuthority() {
        return this.code;
    }
}

