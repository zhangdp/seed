package io.github.seed.controller.sys;

import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.annotation.LogOperation;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.OperateType;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.entity.sys.Permission;
import io.github.seed.model.dto.PermissionTreeNode;
import io.github.seed.service.sys.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
public class ResourceController {

    private final PermissionService permissionService;

    /**
     * 新增权限
     *
     * @param permission
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增权限", description = "新增权限，无需传值id、createTime、updateTime")
    @LogOperation(type = OperateType.CREATE, title = "新增权限", refModule = TableNameConst.SYS_PERMISSION, refIdEl = "#permission.id")
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
    @Operation(summary = "修改权限", description = "修改权限，需传值id")
    @LogOperation(type = OperateType.UPDATE, title = "修改权限", refModule = TableNameConst.SYS_PERMISSION, refIdEl = "#permission.id")
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
    @Operation(summary = "删除权限", description = "根据id删除权限")
    @LogOperation(type = OperateType.DELETE, title = "删除权限", refModule = TableNameConst.SYS_PERMISSION, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return permissionService.delete(id);
    }

    /**
     * 获取树形权限列表
     *
     * @return
     */
    @GetMapping("/tree")
    @Operation(summary = "获取权限树", description = "获取权限树列表")
    public List<PermissionTreeNode> tree() {
        return permissionService.listTree();
    }

    /**
     * 获取当前登录用户的权限树列表
     *
     * @return
     */
    @GetMapping("/tree/user")
    @Operation(summary = "获取用户的权限树", description = "获取当前登录用户的权限树")
    public List<PermissionTreeNode> usersTree(LoginUser loginUser) {
        // todo
        return null;
    }

}
