package io.github.seed.common.security.service;

import io.github.seed.common.security.SecurityConst;
import io.github.seed.common.security.data.AccessToken;
import io.github.seed.common.security.data.RefreshToken;
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
    public void storeAccessToken(AccessToken accessToken, Duration expire) {
        String token = accessToken.getToken();
        String key = this.generateAccessTokenKey(token);
        redisTemplate.opsForValue().set(key, accessToken, expire);
    }

    @Override
    public AccessToken loadAccessToken(String accessToken) {
        return (AccessToken) redisTemplate.opsForValue().get(this.generateAccessTokenKey(accessToken));
    }

    @Override
    public boolean removeAccessToken(String accessToken) {
        return Boolean.TRUE.equals(redisTemplate.delete(this.generateAccessTokenKey(accessToken)));
    }

    @Override
    public boolean updateAccessTokenExpire(String accessToken, Duration expire) {
        return Boolean.TRUE.equals(redisTemplate.expire(this.generateAccessTokenKey(accessToken), expire));
    }

    @Override
    public void storeRefreshToken(RefreshToken refreshToken, Duration expire) {
        String key = generateRefreshTokenKey(refreshToken.getToken());
        redisTemplate.opsForValue().set(key, refreshToken, expire);
    }

    @Override
    public RefreshToken loadRefreshToken(String refreshToken) {
        String key = generateRefreshTokenKey(refreshToken);
        return (RefreshToken) redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean removeRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.delete(this.generateRefreshTokenKey(refreshToken)));
    }

    @Override
    public boolean updateRefreshTokenExpire(String refreshToken, Duration expire) {
        return Boolean.TRUE.equals(redisTemplate.expire(this.generateRefreshTokenKey(refreshToken), expire));
    }

    @Override
    public void storeUserToAccessToken(String username, String accessToken, Duration expire) {
        String key = this.generateUserToAccessKey(username);
        redisTemplate.opsForZSet().add(key, accessToken, System.currentTimeMillis());
        redisTemplate.expire(key, expire);
    }

    @Override
    public int countUserToAccessToken(String username) {
        Long count = redisTemplate.opsForZSet().size(this.generateUserToAccessKey(username));
        return count == null ? 0 : count.intValue();
    }

    @Override
    public void pruneExpiredUserToAccessToken(String username, Duration expire) {
        long score = System.currentTimeMillis() - expire.toMillis();
        redisTemplate.opsForZSet().removeRangeByScore(this.generateUserToAccessKey(username), 0, score);
    }

    @Override
    public void updateUserToAccessTokenExpire(String username, String accessToken, Duration expire) {
        String key = this.generateUserToAccessKey(username);
        redisTemplate.opsForZSet().add(key, accessToken, System.currentTimeMillis());
        redisTemplate.expire(key, expire);
    }

    @Override
    public void removeUserToAccessToken(String username, String accessToken) {
        String key = this.generateUserToAccessKey(username);
        redisTemplate.opsForZSet().remove(key, accessToken);
    }

    /**
     * 生成accessToken redis key
     *
     * @param accessToken
     * @return
     */
    private String generateAccessTokenKey(String accessToken) {
        return SecurityConst.REDIS_ACCESS_TOKEN_PREFIX + SecurityConst.REDIS_SPLIT + accessToken;
    }

    /**
     * 生成用户的access_token redis key
     *
     * @param username
     * @return
     */
    private String generateUserToAccessKey(String username) {
        return SecurityConst.REDIS_USER_TO_ACCESS_PREFIX + SecurityConst.REDIS_SPLIT + username;
    }

    /**
     * 生成刷新令牌 redis key
     *
     * @param refreshToken
     * @return
     */
    private String generateRefreshTokenKey(String refreshToken) {
        return SecurityConst.REDIS_REFRESH_TOKEN_PREFIX + SecurityConst.REDIS_SPLIT + refreshToken;
    }

}
