package io.github.seed.controller;

import io.github.seed.common.security.service.SecurityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
@Deprecated
public class AuthController {

    private final SecurityService securityService;

}
