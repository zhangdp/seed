package io.github.seed.service.sys.impl;

import io.github.seed.entity.sys.RolePermission;
import io.github.seed.mapper.sys.RolePermissionMapper;
import io.github.seed.service.sys.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 角色关联权限service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<RolePermission> listByRoleId(Long roleId) {
        return rolePermissionMapper.selectListByRoleId(roleId);
    }

    @Override
    public List<RolePermission> listByRoleIds(Collection<Long> roleIds) {
        return rolePermissionMapper.selectListByRoleIdIn(roleIds);
    }
}
