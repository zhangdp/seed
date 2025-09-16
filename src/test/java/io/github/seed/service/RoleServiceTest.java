package io.github.seed.service;

import io.github.seed.entity.sys.Role;
import io.github.seed.service.sys.RoleService;
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
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Test
    public void addRole() {
        Role role = new Role();
        role.setCode("ROLE_USER");
        role.setName("普通用户");
        role.setDescription(null);
        roleService.add(role);
    }

    @Test
    public void listUserRoles() {
        Long userId = 1L;
        System.out.println(roleService.listUserRoles(userId));
    }

    @Test
    public void exists() {
        Long id = 1L;
        System.out.println(roleService.exists(id));
    }

}
