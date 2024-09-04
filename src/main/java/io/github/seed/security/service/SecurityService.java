package io.github.seed.security.service;

import io.github.seed.common.constant.Const;
import io.github.seed.security.data.RolePermissionGrantedAuthority;
import io.github.seed.security.SecurityConst;
import io.github.seed.entity.sys.SysResource;
import io.github.seed.entity.sys.SysRole;
import io.github.seed.entity.sys.SysUser;
import io.github.seed.security.data.LoginUser;
import io.github.seed.service.sys.SysResourceService;
import io.github.seed.service.sys.SysRoleService;
import io.github.seed.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.lang.Validator;
import org.dromara.hutool.core.text.StrUtil;
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

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysResourceService sysResourceService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = null;
        if (Validator.isMobile(username)) {
            sysUser = sysUserService.getByMobile(username);
        } else if (Validator.isEmail(username)) {
            sysUser = sysUserService.getByEmail(username);
        }
        if (sysUser == null) {
            sysUser = sysUserService.getByUsername(username);
        }
        Assert.notNull(sysUser, () -> new UsernameNotFoundException("不存在账号：" + username));
        return this.toUserDetails(sysUser);
    }

    /**
     * 转为UserDetails
     *
     * @param sysUser
     * @return
     */
    public UserDetails toUserDetails(SysUser sysUser) {
        LoginUser user = new LoginUser();
        BeanUtils.copyProperties(sysUser, user);
        user.setEnabled(sysUser.getStatus() == Const.GOOD);
        List<RolePermissionGrantedAuthority> authorities = new ArrayList<>();
        user.setAuthorities(authorities);
        List<SysRole> roleList = sysRoleService.listUserRoles(user.getId());
        Set<String> permissionCache = new HashSet<>();
        if (CollUtil.isNotEmpty(roleList)) {
            for (SysRole role : roleList) {
                String roleCode = role.getCode().trim().toUpperCase();
                if (!roleCode.startsWith(SecurityConst.ROLE_PREFIX)) {
                    roleCode = SecurityConst.ROLE_PREFIX + roleCode;
                }
                authorities.add(new RolePermissionGrantedAuthority(roleCode,
                        RolePermissionGrantedAuthority.AuthorityType.ROLE, role.getId()));
                List<SysResource> resources = sysResourceService.listRoleResources(role.getId());
                if (CollUtil.isNotEmpty(resources)) {
                    for (SysResource resource : resources) {
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

}
