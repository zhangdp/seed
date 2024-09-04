package io.github.seed.service;

import io.github.seed.entity.sys.SysRole;
import io.github.seed.entity.sys.SysUser;
import io.github.seed.entity.sys.SysUserRole;
import io.github.seed.service.sys.SysRoleService;
import io.github.seed.service.sys.SysUserRoleService;
import io.github.seed.service.sys.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 2023/4/7
 *
 * @author zhangdp
 * @since
 */
@SpringBootTest
public class SysUserRoleServiceTest {

    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserService sysUserService;

    @Test
    public void add() {
        SysUser user = sysUserService.getByUsername("admin");
        SysRole role = sysRoleService.getByCode("ROLE_ADMIN");
        SysUserRole ur = new SysUserRole();
        ur.setRoleId(role.getId());
        ur.setUserId(user.getId());
        sysUserRoleService.add(ur);
    }
}
