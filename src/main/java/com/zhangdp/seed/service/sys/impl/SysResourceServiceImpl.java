package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.common.constant.CacheConst;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.entity.sys.SysRoleResource;
import com.zhangdp.seed.entity.sys.SysUserRole;
import com.zhangdp.seed.mapper.sys.SysResourceMapper;
import com.zhangdp.seed.service.sys.SysResourceService;
import com.zhangdp.seed.service.sys.SysRoleResourceService;
import com.zhangdp.seed.service.sys.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2023/4/12 资源service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@CacheConfig(cacheNames = CacheConst.CACHE_SYS_RESOURCE)
@RequiredArgsConstructor
@Service
public class SysResourceServiceImpl extends ServiceImpl<SysResourceMapper, SysResource> implements SysResourceService {

    private final SysRoleResourceService sysRoleResourceService;
    private final SysUserRoleService sysUserRoleService;

    @Override
    public List<SysResource> listRoleResources(Long roleId) {
        List<SysRoleResource> pks = sysRoleResourceService.listByRoleId(roleId);
        if (CollUtil.isEmpty(pks)) {
            return null;
        }
        return this.list(Wrappers.lambdaQuery(SysResource.class)
                .in(SysResource::getId, pks.stream().map(SysRoleResource::getResourceId).toList())
                .orderByAsc(SysResource::getParentId)
                .orderByAsc(SysResource::getSorts));
    }

    @Override
    public List<SysResource> listUserResources(Long userId) {
        // 获取该用户的关联角色
        List<SysUserRole> urs = sysUserRoleService.listByUserId(userId);
        if (CollUtil.isEmpty(urs)) {
            return null;
        }
        // 根据角色列表获取关联资源
        List<SysRoleResource> rrs = sysRoleResourceService.listByRoleIds(urs.stream().map(SysUserRole::getRoleId).toList());
        if (CollUtil.isEmpty(rrs)) {
            return null;
        }
        return this.list(Wrappers.lambdaQuery(SysResource.class)
                .in(SysResource::getId, rrs.stream().map(SysRoleResource::getResourceId).toList())
                .orderByAsc(SysResource::getParentId)
                .orderByAsc(SysResource::getSorts));
    }
}
