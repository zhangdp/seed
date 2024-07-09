package com.zhangdp.seed.controller.sys;

import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.annotation.OperationLog;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.entity.sys.SysProperties;
import com.zhangdp.seed.model.params.BaseQueryParams;
import com.zhangdp.seed.model.params.PageQuery;
import com.zhangdp.seed.service.sys.SysPropertiesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 2023/6/21 系统配置controller
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/properties")
@Tag(name = "系统配置", description = "系统配置相关接口")
public class SysPropertiesController {

    private final SysPropertiesService sysPropertiesService;

    /**
     * 新增配置
     *
     * @param model
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增配置", description = "新增配置，无需传值id、createTime、updateTime")
    @OperationLog(type = OperateType.CREATE, title = "新增配置", refModule = TableNameConst.SYS_PROPERTIES, refIdEl = "#model.id")
    public boolean add(@RequestBody @Validated(ValidGroup.Update.class) SysProperties model) {
        return sysPropertiesService.add(model);
    }

    /**
     * 修改配置
     *
     * @param param
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改配置", description = "修改配置，需传值id")
    @OperationLog(type = OperateType.UPDATE, title = "修改配置", refModule = TableNameConst.SYS_PROPERTIES, refIdEl = "#param.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) SysProperties param) {
        return sysPropertiesService.update(param);
    }

    /**
     * 删除配置
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除配置", description = "根据id删除配置")
    @OperationLog(type = OperateType.DELETE, title = "删除配置", refModule = TableNameConst.SYS_PROPERTIES, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return sysPropertiesService.delete(id);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询配置")
    public PageInfo<SysProperties> pageQuery(@RequestBody @Valid PageQuery<BaseQueryParams> pageQuery) {
        return sysPropertiesService.pageQuery(pageQuery);
    }

}
