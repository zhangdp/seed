package io.github.seed.service;

import io.github.seed.service.sys.DictDataService;
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
public class DictDataServiceTest {

    @Autowired
    private DictDataService dictDataService;

    @Test
    public void listByDictId() {
        Long dictId = 1L;
        System.out.println(dictDataService.listByDictId(dictId));
    }
}
