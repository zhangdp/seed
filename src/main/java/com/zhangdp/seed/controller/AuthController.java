package com.zhangdp.seed.controller;

import com.zhangdp.seed.common.R;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.service.sys.SysUserService;
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
    public R<Boolean> update(@RequestBody @Valid SysUser user) {
        return R.success(sysUserService.updateById(user));
    }
}
