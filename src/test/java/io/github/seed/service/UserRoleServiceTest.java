package io.github.seed.service;

import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.User;
import io.github.seed.entity.sys.UserRole;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserRoleService;
import io.github.seed.service.sys.UserService;
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
public class UserRoleServiceTest {

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;

    @Test
    public void add() {
        User user = userService.getByUsername("admin");
        Role role = roleService.getByCode("ROLE_ADMIN");
        UserRole ur = new UserRole();
        ur.setRoleId(role.getId());
        ur.setUserId(user.getId());
        userRoleService.add(ur);
    }
}
