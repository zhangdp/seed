package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.constant.CacheConst;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Permission;
import io.github.seed.mapper.sys.PermissionMapper;
import io.github.seed.model.dto.PermissionTreeNode;
import io.github.seed.service.sys.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * 2023/4/12 资源service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@CacheConfig(cacheNames = TableNameConst.SYS_PERMISSION)
@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final String CACHE_ROLE_RESOURCES = "role_resources" + CacheConst.SPLIT;

    private final PermissionMapper permissionMapper;

    @Override
    public boolean isExists(Long id) {
        return permissionMapper.existsById(id);
    }

    @Cacheable(key = "'" + CACHE_ROLE_RESOURCES + "' + #roleId")
    @Override
    public List<Permission> listRoleResources(Long roleId) {
        return this.permissionMapper.selectListByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Permission permission) {
        if (permission.getParentId() != Const.ROOT_ID) {
            Assert.isTrue(this.isExists(permission.getParentId()), () -> new BizException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return permissionMapper.insert(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Permission permission) {
        if (permission.getParentId() != Const.ROOT_ID) {
            Assert.isTrue(this.isExists(permission.getParentId()), () -> new BizException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return permissionMapper.update(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return this.permissionMapper.deleteById(id) > 0;
    }

    @Override
    public List<PermissionTreeNode> listTree() {
        return this.toTree(permissionMapper.selectAll());
    }

    @Override
    public List<Permission> listRoleResources(Collection<Long> roleIds) {
        return this.permissionMapper.selectListByRoleIdIn(roleIds);
    }
}
