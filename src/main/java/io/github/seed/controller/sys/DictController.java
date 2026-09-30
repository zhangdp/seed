package io.github.seed.controller.sys;

import io.github.seed.common.annotation.RecordLog;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.data.ValidGroup;
import io.github.seed.common.enums.OperateType;
import io.github.seed.entity.sys.Dict;
import io.github.seed.entity.sys.DictData;
import io.github.seed.model.PageData;
import io.github.seed.model.query.BaseTextQuery;
import io.github.seed.model.query.PageQuery;
import io.github.seed.service.sys.DictDataService;
import io.github.seed.service.sys.DictService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典controller
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/dict")
@Tag(name = "字典", description = "字典相关接口")
public class DictController {

    private final DictService dictService;
    private final DictDataService dictDataService;

    /**
     * 新增字典
     *
     * @param model
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('sys:dict:add')")
    @Operation(summary = "新增字典", description = "新增字典，无需传值id、createTime、updateTime")
    @RecordLog(type = OperateType.CREATE, description = "新增字典", refModule = TableNameConst.SYS_DICT)
    public boolean add(@RequestBody @Validated(ValidGroup.Insert.class) Dict model) {
        return dictService.add(model);
    }

    /**
     * 修改字典
     *
     * @param model
     * @return
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('sys:dict:update')")
    @Operation(summary = "修改字典", description = "修改字典，需传值id，字典类型type不允许修改")
    @RecordLog(type = OperateType.UPDATE, description = "修改字典", refModule = TableNameConst.SYS_DICT, refIdEl = "#model.id")
    public boolean update(@RequestBody @Validated(ValidGroup.Update.class) Dict model) {
        return dictService.update(model);
    }

    /**
     * 删除字典
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:dict:delete')")
    @Operation(summary = "删除字典", description = "根据id删除字典，会同时删除其下的字典项")
    @RecordLog(type = OperateType.DELETE, description = "删除字典", refModule = TableNameConst.SYS_DICT, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return dictService.delete(id);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('sys:dict:read')")
    @Operation(summary = "分页查询字典")
    public PageData<Dict> queryPage(@RequestBody @Valid PageQuery<BaseTextQuery> pageQuery) {
        return dictService.queryPage(pageQuery);
    }

    /**
     * 获取所有字典列表
     *
     * @return
     */
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('sys:dict:read')")
    @Operation(summary = "获取所有字典列表")
    public List<Dict> list() {
        return dictService.listAll();
    }

    /* ------------------------------ 字典项 ------------------------------ */

    /**
     * 查询字典项列表
     *
     * @param dictId
     * @return
     */
    @GetMapping("/data/list")
    @PreAuthorize("hasAuthority('sys:dict:read')")
    @Operation(summary = "查询字典项列表")
    public List<DictData> listData(@RequestParam Long dictId) {
        return dictDataService.listByDictId(dictId);
    }

    /**
     * 新增字典项
     *
     * @param model
     * @return
     */
    @PostMapping("/data/add")
    @PreAuthorize("hasAuthority('sys:dict:add')")
    @Operation(summary = "新增字典项", description = "新增字典项，无需传值id、createTime、updateTime")
    @RecordLog(type = OperateType.CREATE, description = "新增字典项", refModule = TableNameConst.SYS_DICT_DATA)
    public boolean addData(@RequestBody @Validated(ValidGroup.Insert.class) DictData model) {
        return dictDataService.add(model);
    }

    /**
     * 修改字典项
     *
     * @param model
     * @return
     */
    @PutMapping("/data/update")
    @PreAuthorize("hasAuthority('sys:dict:update')")
    @Operation(summary = "修改字典项", description = "修改字典项，需传值id，所属字典dictId不允许修改")
    @RecordLog(type = OperateType.UPDATE, description = "修改字典项", refModule = TableNameConst.SYS_DICT_DATA, refIdEl = "#model.id")
    public boolean updateData(@RequestBody @Validated(ValidGroup.Update.class) DictData model) {
        return dictDataService.update(model);
    }

    /**
     * 删除字典项
     *
     * @param id
     * @return
     */
    @DeleteMapping("/data/delete/{id}")
    @PreAuthorize("hasAuthority('sys:dict:delete')")
    @Operation(summary = "删除字典项")
    @RecordLog(type = OperateType.DELETE, description = "删除字典项", refModule = TableNameConst.SYS_DICT_DATA, refIdEl = "#id")
    public boolean deleteData(@PathVariable Long id) {
        return dictDataService.delete(id);
    }
}
