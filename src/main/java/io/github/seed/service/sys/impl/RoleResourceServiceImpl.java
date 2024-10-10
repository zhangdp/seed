package io.github.seed.service.sys.impl;

import io.github.seed.entity.sys.RoleResource;
import io.github.seed.mapper.sys.RoleResourceMapper;
import io.github.seed.service.sys.RoleResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 2023/4/12 角色关联资源service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class RoleResourceServiceImpl implements RoleResourceService {

    private final RoleResourceMapper roleResourceMapper;

    @Override
    public List<RoleResource> listByRoleId(Long roleId) {
        return roleResourceMapper.selectListByRoleId(roleId);
    }

    @Override
    public List<RoleResource> listByRoleIds(Collection<Long> roleIds) {
        return roleResourceMapper.selectListByRoleIdIn(roleIds);
    }
}
