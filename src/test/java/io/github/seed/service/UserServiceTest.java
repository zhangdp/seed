package io.github.seed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.entity.sys.User;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.params.PageQuery;
import io.github.seed.model.params.UserQuery;
import io.github.seed.service.sys.UserService;
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
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void add() {
        User user = new User();
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
    @SneakyThrows
    public void get() {
        System.out.println(objectMapper.writeValueAsString(userService.getByUsername("admin")));
    }

    @Test
    @SneakyThrows
    public void page() {
        PageQuery<UserQuery> pq = new PageQuery<>();
        pq.setPage(1);
        pq.setSize(10);
        UserQuery query = new UserQuery();
        query.setUsername("admin");
        query.setLoginUserId(1L);
        query.setExcludeSelf(true);
        pq.setParams(query);
        PageData<UserInfo> pd = userService.queryPage(pq);
        System.out.println(objectMapper.writeValueAsString(pd));
    }

    @Test
    public void encodePassword() {
        String password = "123456";
        System.out.println(passwordEncoder.encode(password));
    }

    @Test
    public void existsUsername() {
        System.out.println(userService.existsUsername("test"));
    }

    @Test
    public void existsUsernameAndIdNot() {
        System.out.println(userService.existsUsernameAndIdNot("test", 1L));
    }

}
