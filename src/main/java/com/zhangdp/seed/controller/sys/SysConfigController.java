package com.zhangdp.seed.controller.sys;

import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.entity.sys.SysConfig;
import com.zhangdp.seed.model.PageData;
import com.zhangdp.seed.model.params.BaseQueryParams;
import com.zhangdp.seed.model.params.PageQuery;
import com.zhangdp.seed.service.sys.SysConfigService;
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
@RequestMapping("/sys/config")
@Tag(name = "系统配置", description = "系统配置相关接口")
public class SysConfigController {

    private final SysConfigService sysConfigService;

    /**
     * 新增配置
     *
     * @param model
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增配置", description = "新增配置，无需传值id、createTime、updateTime")
    @OperateLog(type = OperateType.CREATE, title = "新增配置", refModule = TableNameConst.SYS_CONFIG, refIdEl = "#model.id")
    public boolean add(@RequestBody @Validated(ValidGroup.Update.class) SysConfig model) {
        return sysConfigService.add(model);
    }

    /**
     * 修改配置
     *
     * @param model
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改配置", description = "修改配置，需传值id")
    @OperateLog(type = OperateType.UPDATE, title = "修改配置", refModule = TableNameConst.SYS_CONFIG, refIdEl = "#model.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) SysConfig model) {
        return sysConfigService.update(model);
    }

    /**
     * 删除配置
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除配置", description = "根据id删除配置")
    @OperateLog(type = OperateType.DELETE, title = "删除配置", refModule = TableNameConst.SYS_CONFIG, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return sysConfigService.delete(id);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询配置")
    public PageData<SysConfig> queryPage(@RequestBody @Valid PageQuery<BaseQueryParams> pageQuery) {
        return sysConfigService.queryPage(pageQuery);
    }

}
