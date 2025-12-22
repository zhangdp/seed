package io.github.seed.common.security.service;

import java.time.Duration;
import java.util.Map;

/**
 * 2025/12/16 token http session持久化
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface TokenHttpSessionStore {

    /**
     * 根据key获取值
     *
     * @param sessionId
     * @param key
     * @return
     */
    Object get(String sessionId, String key);

    /**
     * 设置值，返回旧值（如果有）
     *
     * @param sessionId
     * @param key
     * @param value
     * @return
     */
    Object put(String sessionId, String key, Object value);

    /**
     * 根据key删除，返回旧值（如果有）
     *
     * @param sessionId
     * @param key
     * @return
     */
    Object remove(String sessionId, String key);

    /**
     * 获取全部
     *
     * @param sessionId
     * @return
     */
    Map<String, Object> getAll(String sessionId);

    /**
     * 清理全部
     *
     * @param sessionId
     */
    void clear(String sessionId);

    /**
     * 重置过期时间
     *
     * @param sessionId
     * @param expireTime
     */
    void resetExpireTime(String sessionId, Duration expireTime);
}
