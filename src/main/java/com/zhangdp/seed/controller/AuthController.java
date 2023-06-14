package com.zhangdp.seed.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.zhangdp.seed.common.component.SecurityHelper;
import com.zhangdp.seed.model.LoginResult;
import com.zhangdp.seed.service.sys.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 2023/4/3 认证接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "认证", description = "认证相关接口如登录、注销等")
public class AuthController {

    private final SecurityHelper securityHelper;
    private final SysRoleService sysRoleService;

    /**
     * 登录
     *
     * @param username 账号
     * @param password 密码
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "账号密码登录", description = "根据账号密码登录")
    @SaIgnore
    public LoginResult login(String username, String password) {
        return securityHelper.loginByPassword(username, password);
    }

    /**
     * 注销
     *
     * @return
     */
    @DeleteMapping("/logout")
    @Operation(summary = "注销", description = "注销，如果本来就未登录不报错")
    public void logout() {
        securityHelper.doLogout();
    }

}
