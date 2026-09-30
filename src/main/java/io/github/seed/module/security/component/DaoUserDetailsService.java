package io.github.seed.module.security.component;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.lang.Validator;
import io.github.seed.common.constant.Const;
import io.github.seed.module.security.data.LoginUser;
import io.github.seed.module.security.data.RolePermissionGrantedAuthority;
import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.User;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 从数据库查询用户信息的spring security用户服务
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class DaoUserDetailsService implements UserDetailsService {

    private final UserService userService;
    private final RoleService roleService;
    private final RolePermissionProvider rolePermissionProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        if (Validator.isMobile(username)) {
            user = userService.getByMobile(username);
        } else if (Validator.isEmail(username)) {
            user = userService.getByEmail(username);
        } else {
            user = userService.getByUsername(username);
        }
        if (user == null) {
            throw new UsernameNotFoundException("不存在账号：" + username);
        }
        return this.toUserDetails(user);
    }

    /**
     * 转为UserDetails
     * <br>只装鉴权需要的字段：姓名、头像等档案信息不进令牌，需要时走{@code /auth/user/info}
     * <br>权限来自{@code RolePermissionProvider}的内存缓存，登录也不必查权限表
     *
     * @param user 用户
     * @return 登录用户
     */
    public UserDetails toUserDetails(User user) {
        LoginUser userDetails = new LoginUser();
        userDetails.setId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());

        userDetails.setEnabled(user.getStatus() == Const.GOOD);
        userDetails.setAccountNonExpired(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(true);

        List<Role> roleList = roleService.listUserRoles(userDetails.getId());
        List<RolePermissionGrantedAuthority> authorities;
        if (CollUtil.isEmpty(roleList)) {
            authorities = new ArrayList<>();
        } else {
            List<String> roleCodes = roleList.stream().map(Role::getCode).filter(Objects::nonNull).toList();
            authorities = rolePermissionProvider.listAuthorities(roleCodes);
        }
        userDetails.setAuthorities(authorities);
        return userDetails;
    }
}
