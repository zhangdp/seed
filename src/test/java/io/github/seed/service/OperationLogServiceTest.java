package io.github.seed.service;

import io.github.seed.entity.sys.OperationLog;
import io.github.seed.model.PageData;
import io.github.seed.model.query.OperationLogTextQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.OperationLogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
    public void deleteBatch() {
        List<Long> ids = List.of(1L, 2L, 3L);
        System.out.println(operationLogService.deleteBatch(ids));
    }

    @Test
    public void pageQuery() {
        PageQuery<OperationLogTextQuery> pageQuery = new PageQuery<>();
        pageQuery.setPage(1);
        pageQuery.setSize(10);
        // pageQuery.setOrderBy("id desc");
        pageQuery.setOrderBy(null);
        OperationLogTextQuery params = new OperationLogTextQuery();
        // params.setUserId(1L);
        params.setEndTime("2025-10-20");
        pageQuery.setParams(params);
        pageQuery.setTotal(-1);
        PageData<OperationLog> pd = operationLogService.queryPage(pageQuery);
        System.out.println(pd);
    }
}
