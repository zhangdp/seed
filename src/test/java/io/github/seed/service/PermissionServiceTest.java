package io.github.seed.service;

import io.github.seed.service.sys.PermissionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 2023/4/12
 *
 * @author zhangdp
 * @since
 */
@SpringBootTest
public class PermissionServiceTest {

    @Autowired
    private PermissionService permissionService;

    @Test
    public void listRoleResources() {
        List<Long> roleIds = List.of(1L, 2L, 3L);
        System.out.println(permissionService.listRoleResources(roleIds));
    }

}
