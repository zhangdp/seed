package io.github.seed.service;

import io.github.seed.entity.sys.SysRole;
import io.github.seed.service.sys.SysRoleService;
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
public class SysRoleServiceTest {

    @Autowired
    private SysRoleService sysRoleService;

    @Test
    public void addRole() {
        SysRole role = new SysRole();
        role.setCode("ROLE_ADMIN");
        role.setName("管理员");
        role.setDescription("");
        sysRoleService.add(role);
    }

    @Test
    public void listUserRoles() {
        Long userId = 1L;
        System.out.println(sysRoleService.listUserRoles(userId));
    }

}
