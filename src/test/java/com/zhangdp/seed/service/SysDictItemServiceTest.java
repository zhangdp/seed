package com.zhangdp.seed.service;

import com.zhangdp.seed.service.sys.SysDictItemService;
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
public class SysDictItemServiceTest {

    @Autowired
    private SysDictItemService sysDictItemService;

    @Test
    public void listByDictId() {
        Long dictId = 1L;
        System.out.println(sysDictItemService.listByDictId(dictId));
    }
}
