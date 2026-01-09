package io.github.seed.controller.sys;

import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.annotation.RecordOperationLog;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.OperateType;
import io.github.seed.entity.sys.Config;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.sys.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class ConfigController {

    private final ConfigService configService;

    /**
     * 新增配置
     *
     * @param model
     * @return
     */
    @PostMapping("/add")
    @Operation(summary = "新增配置", description = "新增配置，无需传值id、createTime、updateTime")
    @RecordOperationLog(type = OperateType.CREATE, description = "新增配置", refModule = TableNameConst.SYS_CONFIG, refIdEl = "#model.id")
    public boolean add(@RequestBody @Validated(ValidGroup.Insert.class) Config model) {
        return configService.add(model);
    }

    /**
     * 修改配置
     *
     * @param model
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改配置", description = "修改配置，需传值id")
    @RecordOperationLog(type = OperateType.UPDATE, description = "修改配置", refModule = TableNameConst.SYS_CONFIG, refIdEl = "#model.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) Config model) {
        return configService.update(model);
    }

    /**
     * 删除配置
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除配置", description = "根据id删除配置")
    @RecordOperationLog(type = OperateType.DELETE, description = "删除配置", refModule = TableNameConst.SYS_CONFIG, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return configService.delete(id) != null;
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询配置")
    public PageData<Config> queryPage(@RequestBody @Valid PageQuery<BaseQueryParams> pageQuery) {
        return configService.queryPage(pageQuery);
    }

    /**
     * 获取所有配置列表
     *
     * @return
     */
    @PostMapping("/list")
    @Operation(summary = "获取所有配置列表")
    public List<Config> list() {
        return configService.listAll();
    }

}
