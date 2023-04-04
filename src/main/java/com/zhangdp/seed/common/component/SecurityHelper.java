package com.zhangdp.seed.common.component;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.zhangdp.seed.common.constant.SecurityConst;
import com.zhangdp.seed.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;

/**
 * 2023/4/4 认证相关帮助类
 * <br>必须是http 请求生命周期内才可操作
 *
 * @author zhangdp
 * @since 1.0.0
 */
@ConditionalOnWebApplication
@Component
@Slf4j
public class SecurityHelper {

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
    public User loginUser() {
        return this.getSessionAttr(SecurityConst.SESSION_USER, User.class);
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

}
