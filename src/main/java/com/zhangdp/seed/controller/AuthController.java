package com.zhangdp.seed.controller;

import com.zhangdp.seed.common.R;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.service.sys.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private SysUserService sysUserService;

    /**
     * 登陆
     *
     * @param username 账号
     * @param password 密码
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "账号密码登陆", description = "根据账号密码登陆")
    public R<SysUser> login(String username, String password) {
        return R.success(sysUserService.getByUsername(username));
    }

    /**
     * 修改
     *
     * @param user 用户
     * @return 是否成功
     */
    @PostMapping("/update")
    @Operation(summary = "修改用户信息", description = "修改自己的用户信息")
    public R<Boolean> update(@RequestBody @Valid SysUser user) {
        return R.success(sysUserService.updateById(user));
    }
}
