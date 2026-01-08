package io.github.seed.service.sys;

import io.github.seed.entity.sys.RolePermission;

import java.util.Collection;
import java.util.List;

/**
 * 角色关联权限service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface RolePermissionService {

    /**
     * 根据角色id获取资源列表
     *
     * @param roleId
     * @return
     */
    List<RolePermission> listByRoleId(Long roleId);

    /**
     * 根据角色id列表获取资源列表
     *
     * @param roleIds
     * @return
     */
    List<RolePermission> listByRoleIds(Collection<Long> roleIds);
}
