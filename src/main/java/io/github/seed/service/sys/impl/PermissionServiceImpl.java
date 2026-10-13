package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.lang.Assert;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.common.annotation.PublishEvent;
import io.github.seed.common.constant.CacheConst;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.EventConst;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.enums.PermissionType;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Permission;
import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.RolePermission;
import io.github.seed.mapper.sys.PermissionMapper;
import io.github.seed.model.dto.PermissionTreeNode;
import io.github.seed.service.sys.PermissionService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
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
    // 不清缓存的话各节点reload拉到的还是旧的"角色->权限"映射，权限变更最长TTL时间才生效
    @CacheEvict(allEntries = true, condition = "#result == true")
    @PublishEvent(value = EventConst.ROLE_PERMISSION_CHANGE, condition = "#result == true")
    public boolean add(Permission permission) {
        // 不传时的默认值与DDL的DEFAULT一致：全字段insert会把null写进SQL，数据库的DEFAULT不生效
        if (permission.getParentId() == null) {
            permission.setParentId(Const.ROOT_ID);
        }
        if (permission.getKeepAlive() == null) {
            permission.setKeepAlive(Const.NO_FALSE);
        }
        if (permission.getVisible() == null) {
            permission.setVisible(Const.YES_TRUE);
        }
        if (permission.getSorts() == null) {
            permission.setSorts(Const.DEFAULT_SORTS);
        }
        if (permission.getParentId() != Const.ROOT_ID) {
            Assert.isTrue(this.isExists(permission.getParentId()), () -> new BizException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return permissionMapper.insert(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, condition = "#result == true")
    @PublishEvent(value = EventConst.ROLE_PERMISSION_CHANGE, condition = "#result == true")
    public boolean update(Permission permission) {
        // 不传parentId表示不改挂载位置，别拿null去拆箱比较
        if (permission.getParentId() != null && permission.getParentId() != Const.ROOT_ID) {
            Assert.isTrue(this.isExists(permission.getParentId()), () -> new BizException(ErrorCode.RESOURCE_PARENT_NOT_EXISTS));
        }
        return permissionMapper.update(permission) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true, condition = "#result == true")
    @PublishEvent(value = EventConst.ROLE_PERMISSION_CHANGE, condition = "#result == true")
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

    @Override
    public List<PermissionTreeNode> listUserMenuTrees(Long userId) {
        return this.toTree(this.permissionMapper.selectMenuListByUserId(userId));
    }
}
