package com.zhangdp.seed.service;

import com.zhangdp.seed.entity.sys.SysRole;
import com.zhangdp.seed.service.sys.SysRoleService;
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
public class SysRoleServiceTest {

    private final SysRoleService sysRoleService;

    @Test
    public void addRole() {
        SysRole role = new SysRole();
        role.setCode("ROLE_ADMIN");
        role.setName("管理员");
        role.setDescription("");
        sysRoleService.save(role);
    }

}
