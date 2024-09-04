package io.github.seed.controller.sys;

import io.github.seed.common.ValidGroup;
import io.github.seed.common.annotation.OperateLog;
import io.github.seed.common.enums.OperateType;
import io.github.seed.entity.sys.SysDept;
import io.github.seed.model.dto.DeptTreeNode;
import io.github.seed.service.sys.SysDeptService;
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
public class SysDeptController {

    private final SysDeptService sysDeptService;

    /**
     * 获取部门树
     *
     * @return
     */
    @GetMapping("/tree")
    @io.swagger.v3.oas.annotations.Operation(summary = "获取部门树")
    public List<DeptTreeNode> tree() {
        return sysDeptService.listTree();
    }

    /**
     * 新增部门
     *
     * @param dept
     * @return
     */
    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.Operation(summary = "新增部门", description = "新增部门，无需传值id、createTime、updateTime")
    @OperateLog(type = OperateType.CREATE, title = "新增部门", refModule = "sys_dept", refIdEl = "#dept.id")
    public boolean add(@RequestBody @Validated(ValidGroup.Update.class) SysDept dept) {
        return sysDeptService.insert(dept);
    }

    /**
     * 修改部门
     *
     * @param dept
     * @return
     */
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.Operation(summary = "修改部门", description = "修改部门，需传值id")
    @OperateLog(type = OperateType.UPDATE, title = "修改部门", refModule = "sys_dept", refIdEl = "#dept.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) SysDept dept) {
        return sysDeptService.update(dept);
    }

    /**
     * 删除部门
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "删除部门", description = "根据id删除部门")
    @OperateLog(type = OperateType.DELETE, title = "删除部门", refModule = "sys_dept", refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return sysDeptService.delete(id);
    }

}
