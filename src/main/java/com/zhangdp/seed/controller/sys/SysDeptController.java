package com.zhangdp.seed.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.entity.sys.SysDept;
import com.zhangdp.seed.model.dto.DeptTreeNode;
import com.zhangdp.seed.service.sys.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    @Operation(summary = "获取部门树")
    public R<List<DeptTreeNode>> tree() {
        return R.success(sysDeptService.listTree());
    }

    /**
     * 新增部门
     *
     * @param dept
     * @return
     */
    @PostMapping("/add")
    @SaCheckPermission("dept:add")
    @Operation(summary = "新增部门", description = "新增用户，无需传值id、createTime、updateTime")
    @OperateLog(type = OperateType.CREATE, refModule = "sys_dept", refIdEl = "#dept.id")
    public R<Boolean> add(@RequestBody @Valid SysDept dept) {
        return R.success(sysDeptService.insert(dept));
    }

}
