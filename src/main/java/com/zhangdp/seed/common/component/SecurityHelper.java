package com.zhangdp.seed.common.component;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.constant.SecurityConst;
import com.zhangdp.seed.entity.sys.SysResource;
import com.zhangdp.seed.entity.sys.SysRole;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.model.LoginResult;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.service.sys.SysResourceService;
import com.zhangdp.seed.service.sys.SysRoleService;
import com.zhangdp.seed.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.date.DateUtil;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 2023/4/4 认证相关帮助类
 * <br>必须是http 请求生命周期内才可操作
 *
 * @author zhangdp
 * @since 1.0.0
 */
@ConditionalOnWebApplication
@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityHelper implements StpInterface {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final SysResourceService sysResourceService;

    /**
     * 获取session对象
     *
     * @return
     */
    private SaSession getSession() {
        return StpUtil.getTokenSession();
    }

    /**
     * 获取当前登录用户id
     *
     * @return
     */
    public Long loginUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public UserInfo loginUser() {
        return this.getSessionAttr(SecurityConst.SESSION_USER, UserInfo.class);
    }

    /**
     * 获取session指定属性值
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getSessionAttr(String key, Class<T> clazz) {
        SaSession session = this.getSession();
        return session.getModel(key, clazz);
    }

    /**
     * 获取session指定属性值
     *
     * @param key
     * @param defaultValue
     * @param <T>
     * @return
     */
    public <T> T getSessionAttr(String key, T defaultValue) {
        SaSession session = this.getSession();
        return session.get(key, defaultValue);
    }

    /**
     * 往session放置属性
     *
     * @param key
     * @param value
     * @return
     */
    public void putSessionAttr(String key, Object value) {
        SaSession session = this.getSession();
        // Object old = session.get(key);
        session.set(key, value);
        // return old;
    }

    /**
     * 设置当前登录用户信息到session中
     *
     * @param user
     */
    private void putLoginUser(UserInfo user) {
        this.putSessionAttr(SecurityConst.SESSION_USER, user);
    }

    /**
     * 账号密码登陆
     *
     * @param username
     * @param password
     * @return
     */
    public LoginResult doLogin(String username, String password) {
        SysUser sysUser = sysUserService.getByUsername(username);
        Assert.notNull(sysUser, "账号不存在或者密码错误");
        this.checkUser(sysUser);
        UserInfo user = this.toUser(sysUser);

        StpUtil.login(sysUser.getId());
        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        this.putLoginUser(user);

        LoginResult ret = new LoginResult();
        ret.setUserId(sysUser.getId());
        ret.setAccessToken(tokenInfo.getTokenValue());
        ret.setUser(user);
        return ret;
    }

    /**
     * 检查账号是否锁定等
     *
     * @param sysUser
     */
    public void checkUser(SysUser sysUser) {
        Assert.isTrue(sysUser.getStatus() == CommonConst.GOOD, "账号已被锁定");
        long dt = StpUtil.getDisableTime(sysUser.getId());

        Assert.isTrue(dt == -2, dt == -1 ? "账号已被永久封禁"
                : "账号已被封禁，解封时间：" + DateUtil.format(LocalDateTime.now().plusSeconds(60L), CommonConst.DATETIME_FORMATTER));
    }

    /**
     * 注销
     *
     * @return
     */
    public void doLogout() {
        StpUtil.logout();
    }

    /**
     * 转为user
     *
     * @param sysUser
     * @return
     */
    private UserInfo toUser(SysUser sysUser) {
        UserInfo user = new UserInfo();
        BeanUtils.copyProperties(sysUser, user, "password");
        return user;
    }

    /**
     * 获取指定账号id所拥有的权限列表
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<SysResource> resources = sysResourceService.listUserResources(Long.parseLong(String.valueOf(loginId)));
        if (CollUtil.isEmpty(resources)) {
            return new ArrayList<>();
        }
        return resources.stream().map(SysResource::getPermission).toList();
    }

    /**
     * 获取指定账号id所拥有的角色列表
     *
     * @param loginId   账号id
     * @param loginType 账号类型
     * @return
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<SysRole> roles = sysRoleService.listUserRoles(Long.parseLong(String.valueOf(loginId)));
        if (CollUtil.isEmpty(roles)) {
            return new ArrayList<>();
        }
        return roles.stream().map(SysRole::getCode).toList();
    }
}
