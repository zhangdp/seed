package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.common.constant.CacheConst;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.SeedException;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.entity.sys.SysRoleResource;
import com.zhangdp.seed.mapper.sys.SysResourceMapper;
import com.zhangdp.seed.service.sys.SysResourceService;
import com.zhangdp.seed.service.sys.SysRoleResourceService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final String CACHE_ROLE_RESOURCES = "role_resources" + CacheConst.SPLIT;

    private final SysRoleResourceService sysRoleResourceService;

    @Override
    public boolean isExists(Long id) {
        return this.baseMapper.exists(Wrappers.lambdaQuery(SysResource.class).eq(SysResource::getId, id));
    }

    @Cacheable(key = "'" + CACHE_ROLE_RESOURCES + "' + #roleId")
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
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(SysResource resource) {
        if (resource.getParentId() != CommonConst.ROOT_ID) {
            Assert.isTrue(this.isExists(resource.getParentId()), () -> new SeedException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return this.save(resource);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysResource resource) {
        if (resource.getParentId() != CommonConst.ROOT_ID) {
            Assert.isTrue(this.isExists(resource.getParentId()), () -> new SeedException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return this.updateById(resource);
    }
}
