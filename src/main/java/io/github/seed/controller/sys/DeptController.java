package io.github.seed.controller.sys;

import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.annotation.LogOperation;
import io.github.seed.common.enums.OperateType;
import io.github.seed.entity.sys.Dept;
import io.github.seed.model.dto.DeptTreeNode;
import io.github.seed.service.sys.DeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 2023/5/18 部门controller
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/dept")
@Tag(name = "部门", description = "部门相关接口")
public class DeptController {

    private final DeptService deptService;

    /**
     * 获取部门树
     *
     * @return
     */
    @GetMapping("/tree")
    @Operation(summary = "获取部门树")
    public List<DeptTreeNode> tree() {
        return deptService.listTree();
    }

    /**
     * 新增部门
     *
     * @param dept
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增部门", description = "新增部门，无需传值id、createTime、updateTime")
    @LogOperation(type = OperateType.CREATE, title = "新增部门", refModule = "sys_dept", refIdEl = "#dept.id")
    public boolean add(@RequestBody @Validated(ValidGroup.Update.class) Dept dept) {
        return deptService.insert(dept);
    }

    /**
     * 修改部门
     *
     * @param dept
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改部门", description = "修改部门，需传值id")
    @LogOperation(type = OperateType.UPDATE, title = "修改部门", refModule = "sys_dept", refIdEl = "#dept.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) Dept dept) {
        return deptService.update(dept);
    }

    /**
     * 删除部门
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除部门", description = "根据id删除部门")
    @LogOperation(type = OperateType.DELETE, title = "删除部门", refModule = "sys_dept", refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return deptService.delete(id);
    }

}
