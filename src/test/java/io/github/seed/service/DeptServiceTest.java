package io.github.seed.service;

import io.github.seed.entity.sys.Dept;
import io.github.seed.model.dto.DeptTreeNode;
import io.github.seed.service.sys.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 2025/9/16
 *
 * @author zhangdp
 * @since
 */
@Slf4j
@SpringBootTest
public class DeptServiceTest {

    @Autowired
    private DeptService deptService;

    @Test
    public void add() {
        Dept dept = new Dept();
        dept.setName("XXX公司");
        dept.setParentId(0L);
        dept.setSorts(1);
        boolean ret = deptService.add(dept);
        log.debug("新增部门测试：{}, result={}", dept, ret);
    }

    @Test
    public void listTree() {
        List<DeptTreeNode> tree = deptService.listTree();
        log.debug("获取部门树测试：{}", tree);
    }
}
