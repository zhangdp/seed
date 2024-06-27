package com.zhangdp.seed.controller.sys;

import com.zhangdp.seed.common.annotation.OperationLog;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.common.enums.OperateType;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.model.PageData;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.model.params.PageQuery;
import com.zhangdp.seed.model.params.UserQuery;
import com.zhangdp.seed.service.sys.SysUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 2023/5/12 用户相关接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/user")
@Tag(name = "用户", description = "用户相关接口")
public class SysUserController {

    private final SysUserService sysUserService;

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @PostMapping("/add")
    @io.swagger.v3.oas.annotations.Operation(summary = "新增用户", description = "新增用户，无需传值id、createTime、updateTime")
    @OperationLog(type = OperateType.CREATE, title = "新增用户", refModule = TableNameConst.SYS_USER, refIdEl = "#user.id", logIfError = true)
    public boolean add(@RequestBody @Valid SysUser user) {
        return sysUserService.insert(user);
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.Operation(summary = "修改用户", description = "修改用户，需传值id，无需传createTime、updateTime")
    @OperationLog(type = OperateType.UPDATE, title = "修改用户", refModule = TableNameConst.SYS_USER, refIdEl = "#user.id")
    public boolean update(@RequestBody @Valid SysUser user) {
        return sysUserService.update(user);
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "删除用户", description = "根据id删除用户，如果本来就不存在则返回false")
    @OperationLog(type = OperateType.DELETE, title = "删除用户", refModule = TableNameConst.SYS_USER, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return sysUserService.delete(id);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @io.swagger.v3.oas.annotations.Operation(summary = "分页查询用户")
    public PageData<UserInfo> pageQuery(@RequestBody @Valid PageQuery<UserQuery> pageQuery) {
        return sysUserService.pageQuery(pageQuery);
    }

}
