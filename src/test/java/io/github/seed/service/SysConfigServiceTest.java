package io.github.seed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.entity.sys.SysConfig;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.sys.SysConfigService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;

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
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getByCode() {
        String key = "";
        System.out.println(sysConfigService.getByKey(key));
    }

    @Test
    @SneakyThrows
    public void page() {
        PageQuery<BaseQueryParams> pageQuery = new PageQuery<>(1, 10, new BaseQueryParams("a"));
        pageQuery.setOrderBy("config_key asc, id desc");
        PageData<SysConfig> pd = sysConfigService.queryPage(pageQuery);
        System.out.println(objectMapper.writeValueAsString(pd));
    }
}
