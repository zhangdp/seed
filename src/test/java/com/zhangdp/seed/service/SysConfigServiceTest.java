package com.zhangdp.seed.service;

import com.zhangdp.seed.service.sys.SysConfigService;
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
public class SysConfigServiceTest {

    @Autowired
    private SysConfigService sysConfigService;

    @Test
    public void getByCode() {
        String code = "";
        System.out.println(sysConfigService.getByCode(code));
    }
}
