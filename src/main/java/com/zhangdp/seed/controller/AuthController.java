package com.zhangdp.seed.controller;

import com.zhangdp.seed.common.security.SecurityService;
import com.zhangdp.seed.model.dto.LoginResult;
import com.zhangdp.seed.model.params.PasswordLoginParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "认证", description = "认证相关接口如登录、注销等")
public class AuthController {

    private final SecurityService securityService;

    /**
     * 账号密码登录
     *
     * @param params
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "账号密码登录", description = "支持账号、手机号、邮箱")
    public LoginResult login(@RequestBody @Valid PasswordLoginParams params) {
        return securityService.doLogin(params.getUsername(), params.getPassword());
    }

}
