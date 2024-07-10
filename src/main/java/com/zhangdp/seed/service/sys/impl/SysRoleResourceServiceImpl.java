package com.zhangdp.seed.service.sys.impl;

import com.zhangdp.seed.entity.sys.SysRoleResource;
import com.zhangdp.seed.mapper.sys.SysRoleResourceMapper;
import com.zhangdp.seed.service.sys.SysRoleResourceService;
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
public class SysRoleResourceServiceImpl implements SysRoleResourceService {

    private final SysRoleResourceMapper sysRoleResourceMapper;

    @Override
    public List<SysRoleResource> listByRoleId(Long roleId) {
        return sysRoleResourceMapper.selectListByRoleId(roleId);
    }

    @Override
    public List<SysRoleResource> listByRoleIds(Collection<Long> roleIds) {
        return sysRoleResourceMapper.selectListByRoleIdIn(roleIds);
    }
}
