package com.zhangdp.seed.common.component;

import com.zhangdp.seed.common.enums.ResourceType;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.entity.sys.SysRole;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.model.dto.ResourceTreeNode;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.service.sys.SysResourceService;
import com.zhangdp.seed.service.sys.SysRoleService;
import com.zhangdp.seed.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 2023/4/4 认证相关帮助类
 * <br>必须是http 请求生命周期内才可操作
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Deprecated
@ConditionalOnWebApplication
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityHelper implements UserDetailsService {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysResourceService sysResourceService;

    /**
     * 获取指定账号id所拥有的权限列表
     *
     * @param userId   账号id
     * @param loginType 账号类型
     * @return
     */
    public List<String> getPermissionList(Long userId, String loginType) {
        List<SysRole> roles = sysRoleService.listUserRoles(userId);
        if (CollUtil.isEmpty(roles)) {
            return new ArrayList<>(0);
        }
        Set<String> set = new HashSet<>(32);
        for (SysRole role : roles) {
            List<SysResource> resources = sysResourceService.listRoleResources(role.getId());
            if (CollUtil.isNotEmpty(resources)) {
                for (SysResource resource : resources) {
                    if (StrUtil.isNotBlank(resource.getPermission())) {
                        set.add(resource.getPermission());
                    }
                }
            }
        }
        return new ArrayList<>(set);
    }

    /**
     * 获取指定账号id所拥有的角色列表
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return
     */
    public List<String> getRoleList(Object loginId, String loginType) {
        List<SysRole> roles = sysRoleService.listUserRoles(Long.parseLong(String.valueOf(loginId)));
        if (CollUtil.isEmpty(roles)) {
            return new ArrayList<>(0);
        }
        return roles.stream().map(SysRole::getCode).toList();
    }

    /**
     * 获取某个用户的菜单树列表
     *
     * @param userId
     * @return
     */
    public List<ResourceTreeNode> listUsersMenuTree(Long userId) {
        List<SysRole> roles = sysRoleService.listUserRoles(userId);
        if (CollUtil.isEmpty(roles)) {
            return new ArrayList<>(0);
        }
        Set<SysResource> set = new HashSet<>(32);
        for (SysRole role : roles) {
            List<SysResource> resources = sysResourceService.listRoleResources(role.getId());
            if (CollUtil.isNotEmpty(resources)) {
                for (SysResource resource : resources) {
                    if (resource.getType() == ResourceType.MENU.type()) {
                        set.add(resource);
                    }
                }
            }
        }
        return sysResourceService.toTree(set);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUsername(username);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return this.toUserDetails(sysUser);
    }

    /**
     * 转为UserDetails
     *
     * @param sysUser
     * @return
     */
    private UserDetails toUserDetails(SysUser sysUser) {
        UserInfo user = new UserInfo();
        BeanUtils.copyProperties(sysUser, user);
        // 获取角色列表
        List<SysRole> roleList = sysRoleService.listUserRoles(sysUser.getId());
        if (CollUtil.isNotEmpty(roleList)) {
            List<String> roles = new ArrayList<>(roleList.size());
            Set<String> permissions = new HashSet<>(32);
            for (SysRole role : roleList) {
                roles.add("ROLE_" + role.getCode());
                List<SysResource> resources = sysResourceService.listRoleResources(role.getId());
                if (CollUtil.isNotEmpty(resources)) {
                    for (SysResource resource : resources) {
                        if (StrUtil.isNotBlank(resource.getPermission())) {
                            permissions.add(resource.getPermission());
                        }
                    }
                }
            }
            user.setRoles(roles);
            user.setPermissions(new ArrayList<>(permissions));
        }
        return user;
    }

    public Long loginUserIdDefaultNull() {
        return null;
    }
}
