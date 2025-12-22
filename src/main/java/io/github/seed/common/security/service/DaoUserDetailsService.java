package io.github.seed.common.security.service;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.lang.Assert;
import cn.hutool.v7.core.lang.Validator;
import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.constant.Const;
import io.github.seed.common.security.SecurityConst;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.common.security.data.RolePermissionGrantedAuthority;
import io.github.seed.entity.sys.Resource;
import io.github.seed.entity.sys.Role;
import io.github.seed.entity.sys.User;
import io.github.seed.service.sys.ResourceService;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 2025/12/22 从数据库查询用户信息的spring security用户服务
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class DaoUserDetailsService implements UserDetailsService {

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
     * @param user
     * @return
     */
    public UserDetails toUserDetails(User user) {
        LoginUser userDetails = new LoginUser();
        userDetails.setId(user.getId());
        userDetails.setUsername(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setMobile(user.getMobile());
        userDetails.setName(user.getName());
        userDetails.setEnabled(user.getStatus() == Const.GOOD);
        userDetails.setAccountNonExpired(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(true);
        List<RolePermissionGrantedAuthority> authorities = new ArrayList<>();
        userDetails.setAuthorities(authorities);

        // 获取角色
        List<Role> roleList = roleService.listUserRoles(userDetails.getId());
        if (CollUtil.isNotEmpty(roleList)) {
            List<Long> roleIds = new ArrayList<>(roleList.size());
            for (Role role : roleList) {
                roleIds.add(role.getId());
                String roleCode = role.getCode().trim().toUpperCase();
                if (!roleCode.startsWith(SecurityConst.ROLE_PREFIX)) {
                    roleCode = SecurityConst.ROLE_PREFIX + roleCode;
                }
                authorities.add(new RolePermissionGrantedAuthority(roleCode,
                        RolePermissionGrantedAuthority.AuthorityType.ROLE, role.getId()));

                // 获取权限
                List<Resource> resources = resourceService.listRoleResources(roleIds);
                if (CollUtil.isNotEmpty(resources)) {
                    for (Resource resource : resources) {
                        if (StrUtil.isNotBlank(resource.getPermission())) {
                            String permissionCode = resource.getPermission().trim().toUpperCase();
                            authorities.add(new RolePermissionGrantedAuthority(permissionCode,
                                    RolePermissionGrantedAuthority.AuthorityType.PERMISSION, resource.getId()));
                        }
                    }
                }
            }
        }
        return userDetails;
    }
}
