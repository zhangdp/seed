package io.github.seed.service;

import io.github.seed.entity.log.LoginLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.LoginLogQuery;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.log.LoginLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 登录日志测试类
 *
 * @author zhangdp
 * @since 2025/9/16
 */
@SpringBootTest
public class LoginLogServiceTest {

    @Autowired
    private LoginLogService loginLogService;

    @Test
    public void pageQuery() {
        PageQuery<LoginLogQuery> pageQuery = new PageQuery<>();
        pageQuery.setPage(1);
        pageQuery.setSize(10);
        pageQuery.setOrderBy("id desc");
        LoginLogQuery params = new LoginLogQuery();
        // params.setUserId(1L);
        params.setEndTime("2020-10-20");
        pageQuery.setParams(params);
        PageData<LoginLog> pd = loginLogService.queryPage(pageQuery);
        System.out.println(pd);
    }
}
