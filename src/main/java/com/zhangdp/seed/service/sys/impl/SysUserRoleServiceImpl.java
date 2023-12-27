package com.zhangdp.seed.service.sys.impl;

import com.zhangdp.seed.entity.sys.SysUserRole;
import com.zhangdp.seed.mapper.sys.SysUserRoleMapper;
import com.zhangdp.seed.service.sys.SysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
