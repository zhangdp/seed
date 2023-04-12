package com.zhangdp.seed.service.sys.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.common.constant.CacheConst;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.entity.sys.SysRoleResource;
import com.zhangdp.seed.mapper.sys.SysResourceMapper;
import com.zhangdp.seed.service.sys.SysResourceService;
import com.zhangdp.seed.service.sys.SysRoleResourceService;
import lombok.RequiredArgsConstructor;
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
}
