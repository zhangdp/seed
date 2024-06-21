package com.zhangdp.seed.service.sys.impl;

import com.zhangdp.seed.common.constant.CacheConst;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.entity.sys.SysRoleResource;
import com.zhangdp.seed.mapper.sys.SysResourceMapper;
import com.zhangdp.seed.model.dto.ResourceTreeNode;
import com.zhangdp.seed.service.sys.SysResourceService;
import com.zhangdp.seed.service.sys.SysRoleResourceService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 2023/4/12 资源service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@CacheConfig(cacheNames = TableNameConst.SYS_RESOURCE)
@RequiredArgsConstructor
@Service
public class SysResourceServiceImpl implements SysResourceService {

    private static final String CACHE_ROLE_RESOURCES = "role_resources" + CacheConst.SPLIT;

    private final SysRoleResourceService sysRoleResourceService;
    private final SysResourceMapper sysResourceMapper;

    @Override
    public boolean isExists(Long id) {
        return sysResourceMapper.existsById(id);
    }

    @Cacheable(key = "'" + CACHE_ROLE_RESOURCES + "' + #roleId")
    @Override
    public List<SysResource> listRoleResources(Long roleId) {
        List<SysRoleResource> pks = sysRoleResourceService.listByRoleId(roleId);
        if (CollUtil.isEmpty(pks)) {
            return Collections.emptyList();
        }
        return sysResourceMapper.selectListByIdIn(pks.stream().map(SysRoleResource::getResourceId).distinct().toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(SysResource resource) {
        if (resource.getParentId() != CommonConst.ROOT_ID) {
            Assert.isTrue(this.isExists(resource.getParentId()), () -> new BizException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return sysResourceMapper.insert(resource) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysResource resource) {
        if (resource.getParentId() != CommonConst.ROOT_ID) {
            Assert.isTrue(this.isExists(resource.getParentId()), () -> new BizException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return sysResourceMapper.updateById(resource) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return this.sysResourceMapper.deleteById(id) > 0;
    }

    @Override
    public List<ResourceTreeNode> listTree() {
        return this.toTree(sysResourceMapper.selectAll());
    }
}
