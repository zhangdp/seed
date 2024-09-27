package io.github.seed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.entity.sys.SysUser;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.params.PageQuery;
import io.github.seed.model.params.UserQuery;
import io.github.seed.service.sys.SysUserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void add() {
        SysUser user = new SysUser();
        user.setUsername("test");
        user.setPassword("123456");
        user.setMobile("13900000000");
        user.setGender('M');
        user.setBirthDate(LocalDate.of(2000, 1, 1));
        user.setEmail("test@seed.com");
        user.setName("测试员");
        // sysUserService.save(user);
    }

    @Test
    public void get() {
        System.out.println(sysUserService.getByUsername("admin"));
    }

    @Test
    @SneakyThrows
    public void page() {
        PageQuery<UserQuery> pq = new PageQuery<>();
        pq.setPage(1);
        pq.setSize(10);
        UserQuery query = new UserQuery();
        query.setUsername("admin");
        pq.setParams(query);
        PageData<UserInfo> pd = sysUserService.queryPage(pq);
        System.out.println(objectMapper.writeValueAsString(pd));
    }

    @Test
    public void encodePassword() {
        String password = "123456";
        System.out.println(passwordEncoder.encode(password));
    }

    @Test
    public void existsUsername() {
        System.out.println(sysUserService.existsUsername("test"));
    }

}
