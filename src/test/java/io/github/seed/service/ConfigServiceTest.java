package io.github.seed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.common.constant.Const;
import io.github.seed.entity.sys.Config;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.sys.ConfigService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 2023/4/12
 *
 * @author zhangdp
 * @since
 */
@Slf4j
@SpringBootTest
public class ConfigServiceTest {

    @Autowired
    private ConfigService configService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void add() {
        Config config = new Config();
        config.setConfigKey("DEFAULT_ROLE_ID");
        config.setDescription("每个用户默认拥有的角色id");
        config.setConfigValue("1");
        config.setIsEncrypted(Const.NO_FALSE);
        config.setIsSystem(Const.YES_TRUE);
        boolean ret = configService.add(config);
        log.debug("新增配置：{}, result:{}", config, ret);

    }

    @Test
    public void getByKey() {
        String key = "ACCESS_TOKEN_TTL";
        System.out.println(configService.getByKey(key));
    }

    @Test
    public void getConfigValue() {
        String key = "ACCESS_TOKEN_TTL";
        System.out.println(configService.getConfigValue(key));
    }

    @Test
    @SneakyThrows
    public void page() {
        PageQuery<BaseQueryParams> pageQuery = new PageQuery<>(1, 10, new BaseQueryParams("a"));
        pageQuery.setOrderBy("config_key asc, id desc");
        PageData<Config> pd = configService.queryPage(pageQuery);
        System.out.println(objectMapper.writeValueAsString(pd));
    }
}
