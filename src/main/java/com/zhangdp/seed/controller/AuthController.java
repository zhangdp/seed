package com.zhangdp.seed.controller;

import com.zhangdp.seed.common.R;
import org.springframework.web.bind.annotation.PostMapping;
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
public class AuthController {

    /**
     * 登陆
     *
     * @param username 账号
     * @param password 密码
     * @return
     */
    @PostMapping("/login")
    public R login(String username, String password) {

    }
}
