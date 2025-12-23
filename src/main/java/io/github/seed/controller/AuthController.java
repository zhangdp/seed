package io.github.seed.controller;

import io.github.seed.common.security.data.LoginResult;
import io.github.seed.common.security.service.SecurityService;
import io.github.seed.model.params.LoginParams;
import io.github.seed.model.params.PasswordLoginParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
     * 登录
     *
     * @param loginParams
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public LoginResult login(@RequestBody @Valid LoginParams loginParams, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        return securityService.doLogin(loginParams, request, response);
    }

    /**
     * 注销
     *
     * @param request
     */
    @DeleteMapping("/logout")
    @Operation(summary = "注销")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean logout(HttpServletRequest request) {
        return securityService.doLogout(request);
    }

    /**
     * 检测token
     *
     * @return
     */
    @PostMapping("/token/check")
    public boolean checkToken() {
        return securityService.checkToken();
    }

    /**
     * 刷新token
     *
     * @return
     */
    @PostMapping("/token/refresh")
    public LoginResult refreshToken(String refreshToken) {
        return securityService.refreshToken(refreshToken);
    }

}
