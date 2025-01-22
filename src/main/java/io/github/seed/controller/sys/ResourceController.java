package io.github.seed.controller.sys;

import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.annotation.LogOperation;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.OperateType;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.entity.sys.Resource;
import io.github.seed.model.dto.ResourceTreeNode;
import io.github.seed.service.sys.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
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
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * 新增资源
     *
     * @param resource
     * @return
     */
    @PostMapping("add")
    @Operation(summary = "新增资源", description = "新增资源，无需传值id、createTime、updateTime")
    @LogOperation(type = OperateType.CREATE, title = "新增资源", refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#resource.id")
    public boolean add(@RequestBody @Validated Resource resource) {
        return resourceService.add(resource);
    }

    /**
     * 修改资源
     *
     * @param resource
     * @return
     */
    @PatchMapping("update")
    @Operation(summary = "修改资源", description = "修改资源，需传值id")
    @LogOperation(type = OperateType.UPDATE, title = "修改资源", refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#resource.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) Resource resource) {
        return resourceService.update(resource);
    }

    /**
     * 删除资源
     *
     * @param id
     * @return
     */
    @DeleteMapping("delete/{id}")
    @Operation(summary = "删除资源", description = "根据id删除资源")
    @LogOperation(type = OperateType.DELETE, title = "删除资源", refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return resourceService.delete(id);
    }

    /**
     * 获取树形资源列表
     *
     * @return
     */
    @GetMapping("tree")
    @Operation(summary = "获取资源树", description = "获取资源树列表")
    public List<ResourceTreeNode> tree() {
        return resourceService.listTree();
    }

    /**
     * 获取当前登录用户的菜单树列表
     *
     * @return
     */
    @GetMapping("menu/user/tree")
    @Operation(summary = "获取用户的菜单树", description = "获取当前登录用户的菜单树")
    public List<ResourceTreeNode> usersMenu(LoginUser loginUser) {
        // todo
        return null;
    }

}
