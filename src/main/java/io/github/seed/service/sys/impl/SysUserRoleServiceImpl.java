package io.github.seed.service.sys.impl;

import io.github.seed.entity.sys.SysUserRole;
import io.github.seed.mapper.sys.SysUserRoleMapper;
import io.github.seed.service.sys.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2023/4/3 用户角色service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl implements SysUserRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<SysUserRole> listByUserId(Long userId) {
        return sysUserRoleMapper.selectListByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(SysUserRole entity) {
        return sysUserRoleMapper.insert(entity) > 0;
    }
}
