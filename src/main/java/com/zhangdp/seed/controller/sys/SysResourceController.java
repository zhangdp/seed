package com.zhangdp.seed.controller.sys;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.service.sys.SysResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 新增资源
     *
     * @param resource
     * @return
     */
    @PostMapping("add")
    @SaCheckPermission(TableNameConst.SYS_RESOURCE + TableNameConst.SPLIT + "add")
    @Operation(summary = "新增资源", description = "新增资源，无需传值id、createTime、updateTime")
    @OperateLog(type = OperateType.CREATE, refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#resource.id")
    public boolean add(@RequestBody @Validated SysResource resource) {
        return sysResourceService.insert(resource);
    }

    /**
     * 修改资源
     *
     * @param resource
     * @return
     */
    @PatchMapping("update")
    @SaCheckPermission(TableNameConst.SYS_RESOURCE + TableNameConst.SPLIT + "update")
    @Operation(summary = "修改资源", description = "修改资源，需传值id")
    @OperateLog(type = OperateType.UPDATE, refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#resource.id")
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
    @SaCheckPermission(TableNameConst.SYS_RESOURCE + TableNameConst.SPLIT + "delete")
    @Operation(summary = "删除资源", description = "根据id删除资源")
    @OperateLog(type = OperateType.DELETE, refModule = TableNameConst.SYS_RESOURCE, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return sysResourceService.removeById(id);
    }

}
