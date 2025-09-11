package io.github.seed.controller.sys;

import io.github.seed.common.annotation.LogOperation;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.OperateType;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.AddUserDto;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.params.PageQuery;
import io.github.seed.model.params.UserQuery;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.service.sys.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class UserController {

    private final UserService userService;

    /**
     * 新增用户
     *
     * @param user
     * @return
     */
    @PostMapping("/add")
    @PreAuthorize("hasPermission('sys:user:add')")
    @Operation(summary = "新增用户", description = "新增用户，无需传值id、createTime、updateTime")
    @LogOperation(type = OperateType.CREATE, title = "新增用户", refModule = TableNameConst.SYS_USER)
    public boolean add(@RequestBody @Valid AddUserDto user) {
        return userService.add(user);
    }

    /**
     * 修改用户
     *
     * @param user
     * @return
     */
    @PutMapping("/update")
    @Operation(summary = "修改用户", description = "修改用户，需传值id，无需传createTime、updateTime")
    @LogOperation(type = OperateType.UPDATE, title = "修改用户", refModule = TableNameConst.SYS_USER, refIdEl = "#user.id")
    public boolean update(@RequestBody @Valid AddUserDto user) {
        return userService.update(user);
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除用户", description = "根据id删除用户，如果本来就不存在则返回false")
    @LogOperation(type = OperateType.DELETE, title = "删除用户", refModule = TableNameConst.SYS_USER, refIdEl = "#id")
    public boolean delete(@PathVariable Long id) {
        return userService.delete(id);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询用户")
    @LogOperation(type = OperateType.READ, title = "分页查询用户", refModule = TableNameConst.SYS_USER)
    public PageData<UserInfo> queryPage(@RequestBody @Valid PageQuery<UserQuery> pageQuery, LoginUser loginUser) {
        UserQuery params = pageQuery.getParams();
        if (params == null) {
            params = new UserQuery();
            pageQuery.setParams(params);
        }
        params.setLoginUserId(loginUser.getId());
        return userService.queryPage(pageQuery);
    }

}
