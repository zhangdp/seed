package com.zhangdp.seed.common.security;

import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.entity.sys.SysRole;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.service.sys.SysResourceService;
import com.zhangdp.seed.service.sys.SysRoleService;
import com.zhangdp.seed.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.lang.Validator;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

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
        SysUser sysUser;
        if (Validator.isMobile(username)) {
            sysUser = sysUserService.getByMobile(username);
        } else if (Validator.isEmail(username)) {
            sysUser = sysUserService.getByEmail(username);
        } else {
            sysUser = sysUserService.getByUsername(username);
        }
        if (sysUser == null) {
            throw new UsernameNotFoundException("不存在账号：" + username);
        }
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
        user.setEnabled(sysUser.getStatus() == CommonConst.GOOD);
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
