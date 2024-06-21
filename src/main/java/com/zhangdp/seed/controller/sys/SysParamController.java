package com.zhangdp.seed.controller.sys;

import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.annotation.Operation;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.entity.sys.SysParam;
import com.zhangdp.seed.model.query.BaseQueryParams;
import com.zhangdp.seed.model.query.PageQuery;
import com.zhangdp.seed.service.sys.SysParamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 2023/6/21 系统参数controller
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/param")
@Tag(name = "系统参数", description = "系统参数相关接口")
public class SysParamController {

    private final SysParamService sysParamService;

    /**
     * 新增参数
     *
     * @param param
     * @return
     */
    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.Operation(summary = "新增参数", description = "新增参数，无需传值id、createTime、updateTime")
    @Operation(type = OperateType.CREATE, title = "新增参数", refModule = "sys_param", refIdEl = "#param.id")
    public boolean add(@RequestBody @Validated(ValidGroup.Update.class) SysParam param) {
        return sysParamService.add(param);
    }

    /**
     * 修改参数
     *
     * @param param
     * @return
     */
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.Operation(summary = "修改参数", description = "修改参数，需传值id")
    @Operation(type = OperateType.UPDATE, title = "修改参数", refModule = "sys_param", refIdEl = "#param.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) SysParam param) {
        return sysParamService.update(param);
    }

    /**
     * 删除参数
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "删除参数", description = "根据id删除参数")
    @Operation(type = OperateType.DELETE, title = "删除参数", refModule = "sys_param", refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return sysParamService.delete(id);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @io.swagger.v3.oas.annotations.Operation(summary = "分页查询参数")
    public PageInfo<SysParam> pageQuery(@RequestBody @Valid PageQuery<BaseQueryParams> pageQuery) {
        return sysParamService.pageQuery(pageQuery);
    }

}
