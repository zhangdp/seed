package io.github.seed.service;

import io.github.seed.service.sys.ResourceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 2023/4/12
 *
 * @author zhangdp
 * @since
 */
@SpringBootTest
public class ResourceServiceTest {

    @Autowired
    private ResourceService resourceService;

    @Test
    public void listRoleResources() {
        Long roleId = 1L;
        System.out.println(resourceService.listRoleResources(roleId));
    }

}
