package io.github.seed.common.security.service;

import io.github.seed.common.constant.CacheConst;
import io.github.seed.common.security.SecurityConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 2025/12/16 token http session持久化之redis实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisTokenHttpSessionStore implements TokenHttpSessionStore {

    /**
     * redis key前缀
     */
    private static final String KEY_PREFIX = SecurityConst.REDIS_PREFIX + CacheConst.SPLIT + "token_session";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * redis存储key
     *
     * @param sessionId
     * @return
     */
    public String redisKey(String sessionId) {
        return KEY_PREFIX + sessionId;
    }

    @Override
    public Object get(String sessionId, String key) {
        String redisKey = this.redisKey(sessionId);
        Object v = redisTemplate.opsForHash().get(redisKey, key);
        log.debug("从redis中获取session值，redisKey={}, key={}, value={}", sessionId, key, v);
        return v;
    }

    @Override
    public Object put(String sessionId, String key, Object value) {
        String redisKey = this.redisKey(sessionId);
        Object old = redisTemplate.opsForHash().get(redisKey, key);
        redisTemplate.opsForHash().put(redisKey, key, value);
        log.debug("设置redis中session值，redisKey={}, key={}, value={}, oldValue={}", sessionId, key, value, old);
        return old;
    }

    @Override
    public Object remove(String sessionId, String key) {
        String redisKey = this.redisKey(sessionId);
        Object old = redisTemplate.opsForHash().get(redisKey, key);
        redisTemplate.opsForHash().delete(redisKey, key);
        log.debug("从redis中删除session值，redisKey={}, key={}, oldValue={}", sessionId, key, old);
        return old;
    }

    @Override
    public Map<String, Object> getAll(String sessionId) {
        String redisKey = this.redisKey(sessionId);
        Map<Object, Object> map = redisTemplate.opsForHash().entries(redisKey);
        Map<String, Object> result;
        if (map.isEmpty()) {
            result = Collections.emptyMap();
        } else {
            result = new LinkedHashMap<>(map.size());
            for (Map.Entry<Object, Object> entity : map.entrySet()) {
                result.put(entity.getKey().toString(), entity.getValue());
            }
        }
        log.debug("从redis中获取全部session值，redisKey={}, map={}", sessionId, result);
        return result;
    }

    @Override
    public void clear(String sessionId) {
        String redisKey = this.redisKey(sessionId);
        redisTemplate.delete(redisKey);
        log.debug("从redis中删除全部session，redisKey={}", redisKey);
    }

    @Override
    public void resetExpireTime(String sessionId, Duration expireTime) {
        String redisKey = this.redisKey(sessionId);
        Boolean ret = redisTemplate.expire(redisKey, expireTime);
        log.debug("重置redis中session过期时间，redisKey={}, expireTime={}, result={}", sessionId, expireTime, ret);
    }
}
