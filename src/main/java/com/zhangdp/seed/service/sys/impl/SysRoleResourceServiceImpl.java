package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.entity.sys.SysRoleResource;
import com.zhangdp.seed.mapper.sys.SysRoleResourceMapper;
import com.zhangdp.seed.service.sys.SysRoleResourceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2023/4/12 角色关联资源service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class SysRoleResourceServiceImpl extends ServiceImpl<SysRoleResourceMapper, SysRoleResource> implements SysRoleResourceService {

    @Override
    public List<SysRoleResource> listByRoleId(Long roleId) {
        return this.list(Wrappers.lambdaQuery(SysRoleResource.class).eq(SysRoleResource::getRoleId, roleId));
    }
}
