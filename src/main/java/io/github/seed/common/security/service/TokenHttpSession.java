package io.github.seed.common.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * token httpsession，类似传统cookie-session一样方便在登录有效期间存取自定义数据
 *
 * @author zhangdp
 * @since 2025/12/16
 */
@Slf4j
@RequiredArgsConstructor
public class TokenHttpSession {

    /**
     * session缓存，减少多次读持久化
     */
    private final static ThreadLocal<Map<String, Object>> sessionCache = ThreadLocal.withInitial(HashMap::new);
    /**
     * session持久化
     */
    private final TokenHttpSessionStore tokenHttpSessionStore;

    /**
     * 根据key获取值
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        String sessionId = this.getSessionId();
        Map<String, Object> map = sessionCache.get();
        if (map.containsKey(key)) {
            Object v = map.get(key);
            log.debug("从本地缓存中获取session值，sessionId={}, key={}, value={}", sessionId, key, v);
        }
        Object value = tokenHttpSessionStore.get(sessionId, key);
        map.put(key, value);
        log.debug("从持久化中获取session值，sessionId={}, key={}, value={}", sessionId, key, value);
        return value;
    }

    /**
     * 设置值
     *
     * @param key
     * @param value
     * @return
     */
    public Object put(String key, Object value) {
        String sessionId = this.getSessionId();
        Object old = tokenHttpSessionStore.put(sessionId, key, value);
        Map<String, Object> map = sessionCache.get();
        map.put(key, value);
        log.debug("设置session值，sessionId={}, key={}, value={}, oldValue={}", sessionId, key, value, old);
        return old;
    }

    /**
     * 移除
     *
     * @param key
     * @return
     */
    public Object remove(String key) {
        String sessionId = this.getSessionId();
        Object old = tokenHttpSessionStore.remove(sessionId, key);
        Map<String, Object> map = sessionCache.get();
        map.remove(key);
        log.debug("删除session值，sessionId={}, key={}, oldValue={}", sessionId, key, old);
        return old;
    }

    /**
     * 获取全部
     *
     * @return
     */
    public Map<String, Object> getAll() {
        String sessionId = this.getSessionId();
        Map<String, Object> map = tokenHttpSessionStore.getAll(sessionId);
        Map<String, Object> localMap = sessionCache.get();
        localMap.clear();
        localMap.putAll(map);
        log.debug("获取全部session值，sessionId={}, map={}", sessionId, map);
        return map;
    }

    /**
     * 清除全部
     */
    public void clear() {
        String sessionId = this.getSessionId();
        tokenHttpSessionStore.clear(sessionId);
        Map<String, Object> map = sessionCache.get();
        map.clear();
        log.debug("清除全部session值，sessionId={}", sessionId);
    }

    /**
     * 清理本地缓存
     * <br>每次请求结束均需要调用此方法
     */
    public void resetLocalCache() {
        sessionCache.remove();
        log.debug("重置session threadLocal缓存");
    }

    /**
     * 重置过期时间
     *
     * @param expireTime
     */
    public void resetExpireTime(long expireTime) {
        if (expireTime <= 0) {
            return;
        }
        String sessionId = this.getSessionId();
        tokenHttpSessionStore.resetExpireTime(sessionId, Duration.ofSeconds(expireTime));
    }

    /**
     * 获取登录唯一标识
     *
     * @return
     */
    private String getSessionId() {
        // todo
        return null;
    }

}
