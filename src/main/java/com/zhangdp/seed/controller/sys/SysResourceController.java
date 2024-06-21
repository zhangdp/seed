package com.zhangdp.seed.controller.sys;

import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.annotation.LoginUserId;
import com.zhangdp.seed.common.annotation.Operation;
import com.zhangdp.seed.common.component.SecurityHelper;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.model.dto.ResourceTreeNode;
import com.zhangdp.seed.service.sys.SysResourceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 2023/6/14 资源相关接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(TableNameConst.PREFIX_SYS + "/resource")
@Tag(name = "资源", description = "资源相关接口")
public class SysResourceController {

    private final SysResourceService sysResourceService;
    private final SecurityHelper securityHelper;

    /**
     * 新增资源
     *
     * @param resource
     * @return
     */
    @PostMapping("add")
    @io.swagger.v3.oas.annotations.Operation(summary = "新增资源", description = "新增资源，无需传值id、createTime、updateTime")
    @Operation(type = OperateType.CREATE, title = "新增资源", refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#resource.id")
    public boolean add(@RequestBody @Validated SysResource resource) {
        return sysResourceService.add(resource);
    }

    /**
     * 修改资源
     *
     * @param resource
     * @return
     */
    @PatchMapping("update")
    @io.swagger.v3.oas.annotations.Operation(summary = "修改资源", description = "修改资源，需传值id")
    @Operation(type = OperateType.UPDATE, title = "修改资源", refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#resource.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) SysResource resource) {
        return sysResourceService.update(resource);
    }

    /**
     * 删除资源
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "删除资源", description = "根据id删除资源")
    @Operation(type = OperateType.DELETE, title = "删除资源", refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return sysResourceService.delete(id);
    }

    /**
     * 获取树形资源列表
     *
     * @return
     */
    @GetMapping("tree")
    @io.swagger.v3.oas.annotations.Operation(summary = "获取资源树", description = "获取资源树列表")
    public List<ResourceTreeNode> tree() {
        return sysResourceService.listTree();
    }

    /**
     * 获取当前登录用户的菜单树列表
     *
     * @param userId
     * @return
     */
    @GetMapping("menu/user/tree")
    @io.swagger.v3.oas.annotations.Operation(summary = "获取用户的菜单树", description = "获取当前登录用户的菜单树")
    public List<ResourceTreeNode> usersMenu(@LoginUserId Long userId) {
        return securityHelper.listUsersMenuTree(userId);
    }

}
