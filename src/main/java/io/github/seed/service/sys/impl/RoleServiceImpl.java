package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.annotation.PublishEvent;
import io.github.seed.common.constant.CacheConst;
import io.github.seed.common.constant.EventConst;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.RolePermission;
import io.github.seed.entity.sys.UserRole;
import io.github.seed.mapper.sys.RoleMapper;
import io.github.seed.mapper.sys.RolePermissionMapper;
import io.github.seed.mapper.sys.UserRoleMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.query.BaseTextQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 2023/4/3 角色service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@CacheConfig(cacheNames = TableNameConst.SYS_ROLE)
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private static final String CACHE_USER_ROLES = "user_roles" + CacheConst.SPLIT;

    private final UserRoleService userRoleService;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;
    private final RoleMapper roleMapper;

    // @Cacheable(key = "'" + CACHE_USER_ROLES + "' + #userId")
    @Override
    public List<Role> listUserRoles(Long userId) {
        return roleMapper.selectListByUserId(userId);
    }

    @Override
    public List<Role> listAll() {
        return roleMapper.selectAll();
    }

    @Override
    public Role getByCode(String code) {
        return roleMapper.selectOneByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PublishEvent(value = EventConst.ROLE_PERMISSION_CHANGE, condition = "#result == true")
    public boolean add(Role entity) {
        Assert.isFalse(roleMapper.existsByCodeAndIdNot(entity.getCode(), null),
                () -> new BizException(ErrorCode.BIZ_ERROR.code(), "角色标识已存在"));
        return roleMapper.insert(entity) > 0;
    }

    @Override
    public boolean exists(Long roleId) {
        return roleMapper.exists(roleId);
    }

    @Override
    public PageData<Role> queryPage(PageQuery<BaseTextQuery> pageQuery) {
        return this.roleMapper.queryPage(pageQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Role entity) {
        Role bean = this.roleMapper.selectOneById(entity.getId());
        Assert.notNull(bean, () -> new BizException(ErrorCode.ROLE_NOT_EXISTS));
        Assert.isFalse(roleMapper.existsByCodeAndIdNot(entity.getCode(), entity.getId()),
                () -> new BizException(ErrorCode.BIZ_ERROR.code(), "角色标识已存在"));
        Role update = new Role();
        update.setId(entity.getId());
        update.setName(entity.getName());
        update.setDescription(entity.getDescription());
        // 标识不允许修改，避免与已分配的权限、缓存绑定关系错乱
        return roleMapper.update(update) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PublishEvent(value = EventConst.ROLE_PERMISSION_CHANGE, condition = "#result == true")
    public boolean delete(Long id) {
        Role bean = this.roleMapper.selectOneById(id);
        if (bean == null) {
            return false;
        }
        // 先解除关联，避免出现脏数据
        rolePermissionMapper.deleteByRoleId(id);
        userRoleMapper.deleteByRoleId(id);
        return roleMapper.deleteById(id) > 0;
    }

    @Override
    public List<Long> listPermissionIds(Long roleId) {
        List<RolePermission> list = rolePermissionMapper.selectListByRoleId(roleId);
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(RolePermission::getPermissionId).distinct().toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PublishEvent(value = EventConst.ROLE_PERMISSION_CHANGE, condition = "#result == true")
    public boolean savePermissions(Long roleId, List<Long> permissionIds) {
        Assert.isTrue(this.roleMapper.exists(roleId), () -> new BizException(ErrorCode.ROLE_NOT_EXISTS));
        rolePermissionMapper.deleteByRoleId(roleId);
        if (CollUtil.isEmpty(permissionIds)) {
            return true;
        }
        List<RolePermission> entities = permissionIds.stream()
                .filter(item -> item != null && item > 0)
                .distinct()
                .map(permissionId -> {
                    RolePermission entity = new RolePermission();
                    entity.setRoleId(roleId);
                    entity.setPermissionId(permissionId);
                    return entity;
                })
                .toList();
        if (CollUtil.isEmpty(entities)) {
            return true;
        }
        return rolePermissionMapper.insertBatch(entities) > 0;
    }
}
