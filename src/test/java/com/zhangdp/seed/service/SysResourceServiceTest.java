package com.zhangdp.seed.service;

import com.zhangdp.seed.service.sys.SysResourceService;
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
public class SysResourceServiceTest {

    @Autowired
    private SysResourceService sysResourceService;

    @Test
    public void listRoleResources() {
        Long roleId = 1L;
        System.out.println(sysResourceService.listRoleResources(roleId));
    }

}
