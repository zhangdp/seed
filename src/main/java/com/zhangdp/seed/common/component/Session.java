package com.zhangdp.seed.common.component;

import cn.dev33.satoken.session.SaSession;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Enumeration;

/**
 * 2023/6/14 satoken session封装
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Hidden
@RequiredArgsConstructor
public class Session {

    private final SaSession saSession;

    public String getId() {
        return saSession.getId();
    }

    public long getCreationTime() {
        return saSession.getCreateTime();
    }

    public Object getAttribute(String name) {
        return saSession.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(saSession.keys());
    }

    public void setAttribute(String name, Object value) {
        saSession.set(name, value);
    }

    public void removeAttribute(String name) {
        saSession.delete(name);
    }

    public <T> T getAttributeOrDefault(String name, T defaultValue) {
        return saSession.get(name, defaultValue);
    }

    public void setAttributeIfAbsent(String name, Object value) {
        saSession.setByNull(name, value);
    }

    public void clear() {
        saSession.clear();
    }
}
