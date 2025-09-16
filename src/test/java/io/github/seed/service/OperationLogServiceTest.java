package io.github.seed.service;

import io.github.seed.entity.log.OperationLog;
import io.github.seed.model.PageData;
import io.github.seed.model.params.OperationLogQuery;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.log.OperationLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 操作日志测试类
 *
 * @author zhangdp
 * @since 2025/9/16
 */
@SpringBootTest
public class OperationLogServiceTest {

    @Autowired
    private OperationLogService operationLogService;

    @Test
    public void pageQuery() {
        PageQuery<OperationLogQuery> pageQuery = new PageQuery<>();
        pageQuery.setPage(1);
        pageQuery.setSize(10);
        // pageQuery.setOrderBy("id desc");
        pageQuery.setOrderBy(null);
        OperationLogQuery params = new OperationLogQuery();
        // params.setUserId(1L);
        params.setEndTime("2025-10-20");
        pageQuery.setParams(params);
        pageQuery.setTotal(-1);
        PageData<OperationLog> pd = operationLogService.queryPage(pageQuery);
        System.out.println(pd);
    }
}
