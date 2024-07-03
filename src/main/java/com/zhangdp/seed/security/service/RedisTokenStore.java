package com.zhangdp.seed.security.service;

import com.zhangdp.seed.security.SecurityConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 2024/6/28 redis方式保存token信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RedisTokenStore implements TokenStore {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void storeAccessToken(String token, UserDetails userDetails, long expiresIn) {
        String key = this.generateKey(token);
        redisTemplate.opsForValue().set(key, userDetails, Duration.ofSeconds(expiresIn));
    }

    @Override
    public UserDetails loadAccessToken(String token) {
        return (UserDetails) redisTemplate.opsForValue().get(this.generateKey(token));
    }

    @Override
    public boolean removeAccessToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.delete(this.generateKey(token)));
    }

    @Override
    public boolean existsAccessToken(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(generateKey(token)));
    }

    @Override
    public long getAccessTokenExpiresIn(String token) {
        Long ttl = redisTemplate.getExpire(this.generateKey(token), TimeUnit.SECONDS);
        return ttl == null ? -2 : ttl;
    }

    @Override
    public boolean updateAccessTokenExpiresIn(String token, long expiresIn) {
        return Boolean.TRUE.equals(redisTemplate.expire(this.generateKey(token), expiresIn, TimeUnit.SECONDS));
    }

    /**
     * 生成redis key
     *
     * @param token
     * @return
     */
    private String generateKey(String token) {
        return SecurityConst.REDIS_ACCESS_TOKEN_PREFIX + SecurityConst.REDIS_SPLIT + token;
    }
}
