package com.zhangdp.seed.service;

import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.service.sys.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

/**
 * 2023/4/4 用户service测试
 *
 * @author zhangdp
 * @since 1.0.0
 */
@SpringBootTest
public class SysUserServiceTest {

    @Autowired
    private SysUserService sysUserService;

    @Test
    public void add() {
        SysUser user = new SysUser();
        user.setUsername("test");
        user.setPassword("123456");
        user.setMobile("13900000000");
        user.setSex('M');
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("test@seed.com");
        user.setName("测试员");
        sysUserService.save(user);
    }

    @Test
    public void get() {
        System.out.println(sysUserService.getByUsername("admin"));
    }

}
