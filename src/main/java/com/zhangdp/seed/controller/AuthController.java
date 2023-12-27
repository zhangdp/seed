package com.zhangdp.seed.controller;

import com.zhangdp.seed.common.component.SecurityHelper;
import com.zhangdp.seed.service.sys.SysRoleService;
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
public class AuthController {

    private final SecurityHelper securityHelper;
    private final SysRoleService sysRoleService;

}
