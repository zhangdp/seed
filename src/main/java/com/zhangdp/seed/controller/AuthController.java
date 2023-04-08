package com.zhangdp.seed.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.component.SecurityHelper;
import com.zhangdp.seed.entity.sys.SysRole;
import com.zhangdp.seed.model.LoginResult;
import com.zhangdp.seed.service.sys.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 2023/4/3 认证接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证", description = "认证相关接口如登陆、注销等")
public class AuthController {

    @Autowired
    private SecurityHelper securityHelper;
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 登陆
     *
     * @param username 账号
     * @param password 密码
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "账号密码登陆", description = "根据账号密码登陆")
    public R<LoginResult> login(String username, String password) {
        return R.success(securityHelper.doLogin(username, password));
    }

    /**
     * 注销
     *
     * @return
     */
    @DeleteMapping("/logout")
    @Operation(summary = "注销", description = "注销，如果本来就未登录不报错")
    public R<?> logout() {
        securityHelper.doLogout();
        return R.success();
    }

    /**
     * 获取当前登录用户的角色列表
     *
     * @return
     */
    @GetMapping("/roles")
    @Operation(summary = "获取当前登录用户的角色列表", description = "获取当前登录用户的角色列表")
    public R<List<SysRole>> listRoles() {
        Long userId = StpUtil.getLoginIdAsLong();
        return R.success(sysRoleService.listUserRoles(userId));
    }

}
