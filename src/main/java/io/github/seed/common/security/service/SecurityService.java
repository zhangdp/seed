package io.github.seed.common.security.service;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.lang.Assert;
import cn.hutool.v7.core.lang.Validator;
import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.constant.Const;
import io.github.seed.common.security.data.LoginResult;
import io.github.seed.common.security.data.RolePermissionGrantedAuthority;
import io.github.seed.common.security.SecurityConst;
import io.github.seed.entity.sys.Resource;
import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.User;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.service.sys.ResourceService;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 2024/6/27 认证服务类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityService implements UserDetailsService {

    private final UserService userService;
    private final RoleService roleService;
    private final ResourceService resourceService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        if (Validator.isMobile(username)) {
            user = userService.getByMobile(username);
        } else if (Validator.isEmail(username)) {
            user = userService.getByEmail(username);
        }
        if (user == null) {
            user = userService.getByUsername(username);
        }
        Assert.notNull(user, () -> new UsernameNotFoundException("不存在账号：" + username));
        return this.toUserDetails(user);
    }

    /**
     * 转为UserDetails
     *
     * @param sysUser
     * @return
     */
    public UserDetails toUserDetails(User sysUser) {
        LoginUser user = new LoginUser();
        BeanUtils.copyProperties(sysUser, user);
        user.setEnabled(sysUser.getStatus() == Const.GOOD);
        List<RolePermissionGrantedAuthority> authorities = new ArrayList<>();
        user.setAuthorities(authorities);
        List<Role> roleList = roleService.listUserRoles(user.getId());
        Set<String> permissionCache = new HashSet<>();
        if (CollUtil.isNotEmpty(roleList)) {
            for (Role role : roleList) {
                String roleCode = role.getCode().trim().toUpperCase();
                if (!roleCode.startsWith(SecurityConst.ROLE_PREFIX)) {
                    roleCode = SecurityConst.ROLE_PREFIX + roleCode;
                }
                authorities.add(new RolePermissionGrantedAuthority(roleCode,
                        RolePermissionGrantedAuthority.AuthorityType.ROLE, role.getId()));
                List<Resource> resources = resourceService.listRoleResources(role.getId());
                if (CollUtil.isNotEmpty(resources)) {
                    for (Resource resource : resources) {
                        if (StrUtil.isNotBlank(resource.getPermission())) {
                            String permissionCode = resource.getPermission().trim().toUpperCase();
                            if (permissionCache.add(permissionCode)) {
                                authorities.add(new RolePermissionGrantedAuthority(permissionCode,
                                        RolePermissionGrantedAuthority.AuthorityType.PERMISSION, resource.getId()));
                            }
                        }
                    }
                }
            }
        }
        return user;
    }

    /**
     * 执行登录
     *
     * @return
     */
    public LoginResult doLogin() {
        return null;
    }

    /**
     * 执行注销
     *
     * @return
     */
    public boolean doLogout() {
        return false;
    }

    /**
     * 检测token
     *
     * @return
     */
    public boolean checkToken() {
        return false;
    }

    /**
     * 刷新token
     *
     * @param refreshToken
     * @return
     */
    public LoginResult refreshToken(String refreshToken) {
        return null;
    }
}
