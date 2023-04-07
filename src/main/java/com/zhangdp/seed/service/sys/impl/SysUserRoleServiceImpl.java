package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.entity.sys.SysUserRole;
import com.zhangdp.seed.mapper.sys.SysUserRoleMapper;
import com.zhangdp.seed.service.sys.SysUserRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2023/4/3 用户角色service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

    @Override
    public List<SysUserRole> listByUserId(Long userId) {
        return this.list(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getUserId, userId));
    }
}
