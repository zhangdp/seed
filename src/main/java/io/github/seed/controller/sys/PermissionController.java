package io.github.seed.controller.sys;

import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.annotation.RecordOperationLog;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.OperateType;
import io.github.seed.entity.sys.Permission;
import io.github.seed.model.dto.PermissionTreeNode;
import io.github.seed.service.sys.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限相关接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(TableNameConst.PREFIX_SYS + "/permission")
@Tag(name = "权限", description = "权限相关接口")
public class PermissionController {

    private final PermissionService permissionService;

    /**
     * 新增权限
     *
     * @param permission
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasPermission('sys:permission:create')")
    @Operation(summary = "新增权限", description = "新增权限，无需传值id、createTime、updateTime")
    @RecordOperationLog(type = OperateType.CREATE, description = "新增权限", refModule = TableNameConst.SYS_PERMISSION, refIdEl = "#permission.id")
    public boolean add(@RequestBody @Validated Permission permission) {
        return permissionService.add(permission);
    }

    /**
     * 修改权限
     *
     * @param permission
     * @return
     */
    @PatchMapping("/update")
    @PreAuthorize("hasPermission('sys:permission:update')")
    @Operation(summary = "修改权限", description = "修改权限，需传值id")
    @RecordOperationLog(type = OperateType.UPDATE, description = "修改权限", refModule = TableNameConst.SYS_PERMISSION, refIdEl = "#permission.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) Permission permission) {
        return permissionService.update(permission);
    }

    /**
     * 删除权限
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasPermission('sys:permission:delete')")
    @Operation(summary = "删除权限", description = "根据id删除权限")
    @RecordOperationLog(type = OperateType.DELETE, description = "删除权限", refModule = TableNameConst.SYS_PERMISSION, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return permissionService.delete(id);
    }

    /**
     * 获取树形权限列表
     *
     * @return
     */
    @GetMapping("/tree")
    @PreAuthorize("hasPermission('sys:permission:read')")
    @Operation(summary = "获取权限树", description = "获取权限树列表")
    public List<PermissionTreeNode> tree() {
        return permissionService.listTree();
    }

}
