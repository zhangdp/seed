package io.github.seed.service.sys.impl;

import io.github.seed.common.constant.CacheConst;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.UserRole;
import io.github.seed.mapper.sys.RoleMapper;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RoleMapper roleMapper;

    @Cacheable(key = "'" + CACHE_USER_ROLES + "' + #userId")
    @Override
    public List<Role> listUserRoles(Long userId) {
        List<UserRole> pks = userRoleService.listByUserId(userId);
        if (CollUtil.isEmpty(pks)) {
            return null;
        }
        List<Long> roleIds = pks.stream().map(UserRole::getRoleId).distinct().toList();
        return roleMapper.selectListByRoleIdIn(roleIds);
    }

    @Override
    public Role getByCode(String code) {
        return roleMapper.selectOneByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(Role entity) {
        return roleMapper.insert(entity) > 0;
    }
}
