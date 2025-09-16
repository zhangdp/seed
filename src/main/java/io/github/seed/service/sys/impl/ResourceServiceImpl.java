package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.constant.CacheConst;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Resource;
import io.github.seed.entity.sys.RoleResource;
import io.github.seed.mapper.sys.ResourceMapper;
import io.github.seed.model.dto.ResourceTreeNode;
import io.github.seed.service.sys.ResourceService;
import io.github.seed.service.sys.RoleResourceService;
import lombok.RequiredArgsConstructor;
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
public class ResourceServiceImpl implements ResourceService {

    private static final String CACHE_ROLE_RESOURCES = "role_resources" + CacheConst.SPLIT;

    private final RoleResourceService roleResourceService;
    private final ResourceMapper resourceMapper;

    @Override
    public boolean isExists(Long id) {
        return resourceMapper.existsById(id);
    }

    @Cacheable(key = "'" + CACHE_ROLE_RESOURCES + "' + #roleId")
    @Override
    public List<Resource> listRoleResources(Long roleId) {
        List<RoleResource> pks = roleResourceService.listByRoleId(roleId);
        if (CollUtil.isEmpty(pks)) {
            return Collections.emptyList();
        }
        return resourceMapper.selectListByIdIn(pks.stream().map(RoleResource::getResourceId).distinct().toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Resource resource) {
        if (resource.getParentId() != Const.ROOT_ID) {
            Assert.isTrue(this.isExists(resource.getParentId()), () -> new BizException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return resourceMapper.insert(resource) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Resource resource) {
        if (resource.getParentId() != Const.ROOT_ID) {
            Assert.isTrue(this.isExists(resource.getParentId()), () -> new BizException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return resourceMapper.update(resource) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return this.resourceMapper.deleteById(id) > 0;
    }

    @Override
    public List<ResourceTreeNode> listTree() {
        return this.toTree(resourceMapper.selectAll());
    }

}
