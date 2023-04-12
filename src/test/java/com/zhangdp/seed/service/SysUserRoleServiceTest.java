package com.zhangdp.seed.service;

import com.zhangdp.seed.entity.sys.SysRole;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.entity.sys.SysUserRole;
import com.zhangdp.seed.service.sys.SysRoleService;
import com.zhangdp.seed.service.sys.SysUserRoleService;
import com.zhangdp.seed.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 2023/4/7
 *
 * @author zhangdp
 * @since
 */
@SpringBootTest
@RequiredArgsConstructor
public class SysUserRoleServiceTest {

    private final SysUserRoleService sysUserRoleService;
    private final SysRoleService sysRoleService;
    private final SysUserService sysUserService;

    @Test
    public void add() {
        SysUser user = sysUserService.getByUsername("admin");
        SysRole role = sysRoleService.getByCode("ROLE_ADMIN");
        SysUserRole ur = new SysUserRole();
        ur.setRoleId(role.getId());
        ur.setUserId(user.getId());
        sysUserRoleService.save(ur);
    }
}
