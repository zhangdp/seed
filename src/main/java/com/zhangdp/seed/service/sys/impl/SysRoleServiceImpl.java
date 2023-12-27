package com.zhangdp.seed.service.sys.impl;

import com.zhangdp.seed.common.constant.CacheConst;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.entity.sys.SysRole;
import com.zhangdp.seed.entity.sys.SysUserRole;
import com.zhangdp.seed.mapper.sys.SysRoleMapper;
import com.zhangdp.seed.service.sys.SysRoleService;
import com.zhangdp.seed.service.sys.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
public class SysRoleServiceImpl implements SysRoleService {

    private static final String CACHE_USER_ROLES = "user_roles" + CacheConst.SPLIT;

    private final SysUserRoleService sysUserRoleService;
    private final SysRoleMapper sysRoleMapper;

    @Cacheable(key = "'" + CACHE_USER_ROLES + "' + #userId")
    @Override
    public List<SysRole> listUserRoles(Long userId) {
        List<SysUserRole> pks = sysUserRoleService.listByUserId(userId);
        if (CollUtil.isEmpty(pks)) {
            return null;
        }
        List<Long> roleIds = pks.stream().map(SysUserRole::getRoleId).distinct().toList();
        return sysRoleMapper.selectListByRoleIdIn(roleIds);
    }

    @Override
    public SysRole getByCode(String code) {
        return sysRoleMapper.selectOneByCode(code);
    }
}
