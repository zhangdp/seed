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
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    public LoginResult login(@RequestBody @Valid LoginParams loginParams) throws ServletException, IOException {
        return securityService.login(loginParams);
    }

    /**
     * 注销
     *
     * @param request
     */
    @DeleteMapping("/logout")
    @Operation(summary = "注销", description = "无论结果如何，前端都当做注销成功清除本地token")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean logout(HttpServletRequest request) {
        return securityService.logout(request);
    }

    /**
     * 检测token
     *
     * @return
     */
    @PostMapping("/token/check")
    @Operation(summary = "检测token", description = "检测当前token是否有效，有效时返回true，无效时响应401")
    public boolean checkToken() {
        return securityService.checkToken();
    }

    /**
     * 续签
     *
     * @param refreshToken
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @PostMapping("/token/refresh")
    @Operation(summary = "续签", description = "使用refresh_token续签token，成功时与登录接口无异，失败时响应401")
    public LoginResult refreshToken(String refreshToken) throws ServletException, IOException {
        return securityService.refreshToken(refreshToken);
    }

}
