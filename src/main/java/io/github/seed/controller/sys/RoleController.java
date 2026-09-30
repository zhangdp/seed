package io.github.seed.controller.sys;

import io.github.seed.common.annotation.RecordLog;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.enums.OperateType;
import io.github.seed.entity.sys.Role;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.RolePermissionDto;
import io.github.seed.model.query.BaseTextQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色controller
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/role")
@Tag(name = "角色", description = "角色相关接口")
public class RoleController {

    private final RoleService roleService;

    /**
     * 新增角色
     *
     * @param model
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:role:add')")
    @Operation(summary = "新增角色", description = "新增角色，无需传值id、createTime、updateTime")
    @RecordLog(type = OperateType.CREATE, description = "新增角色", refModule = TableNameConst.SYS_ROLE)
    public boolean add(@RequestBody @Validated(ValidGroup.Insert.class) Role model) {
        return roleService.add(model);
    }

    /**
     * 修改角色
     *
     * @param model
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:role:update')")
    @Operation(summary = "修改角色", description = "修改角色，需传值id，角色标识code不允许修改")
    @RecordLog(type = OperateType.UPDATE, description = "修改角色", refModule = TableNameConst.SYS_ROLE, refIdEl = "#model.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) Role model) {
        return roleService.update(model);
    }

    /**
     * 删除角色
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:role:delete')")
    @Operation(summary = "删除角色", description = "根据id删除角色，会同时解除用户与权限的关联")
    @RecordLog(type = OperateType.DELETE, description = "删除角色", refModule = TableNameConst.SYS_ROLE, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return roleService.delete(id);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sys:role:read')")
    @Operation(summary = "分页查询角色")
    public PageData<Role> queryPage(@RequestBody @Valid PageQuery<BaseTextQuery> pageQuery) {
        return roleService.queryPage(pageQuery);
    }

    /**
     * 获取所有角色列表
     *
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('sys:role:read')")
    @Operation(summary = "获取所有角色列表")
    public List<Role> list() {
        return roleService.listAll();
    }

    /**
     * 查询角色已分配的权限id
     *
     * @param roleId
     * @return
     */
    @GetMapping("/permission/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:read')")
    @Operation(summary = "查询角色已分配的权限id列表")
    public List<Long> listPermissionIds(@PathVariable Long roleId) {
        return roleService.listPermissionIds(roleId);
    }

    /**
     * 分配角色权限
     *
     * @param params
     * @return
     */
    @PutMapping("/permission")
    @PreAuthorize("hasAuthority('sys:role:permission')")
    @Operation(summary = "分配角色权限", description = "全量覆盖，传空数组表示清空该角色的权限")
    @RecordLog(type = OperateType.UPDATE, description = "分配角色权限", refModule = TableNameConst.SYS_ROLE, refIdEl = "#params.roleId")
    public boolean savePermissions(@RequestBody @Valid RolePermissionDto params) {
        return roleService.savePermissions(params.getRoleId(), params.getPermissionIds());
    }
}
