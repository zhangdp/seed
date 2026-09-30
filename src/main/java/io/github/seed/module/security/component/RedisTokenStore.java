package io.github.seed.module.security.component;

import io.github.seed.module.security.data.RefreshToken;
import io.github.seed.module.security.data.SecurityConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * redis方式保存令牌
 * <br>刷新令牌存json对象，用户-凭证索引只存字符串与时间戳，用{@code StringRedisTemplate}免去序列化歧义
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisTokenStore implements TokenStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void storeRefreshToken(RefreshToken refreshToken, Duration expire) {
        redisTemplate.opsForValue().set(this.generateRefreshTokenKey(refreshToken.getToken()), refreshToken, expire);
    }

    @Override
    public RefreshToken loadRefreshToken(String refreshToken) {
        return (RefreshToken) redisTemplate.opsForValue().get(this.generateRefreshTokenKey(refreshToken));
    }

    @Override
    public boolean removeRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.delete(this.generateRefreshTokenKey(refreshToken)));
    }

    @Override
    public void registerUserToken(Long userId, String tokenId, long expireAt) {
        stringRedisTemplate.opsForZSet().add(this.generateUserTokenKey(userId), tokenId, (double) expireAt);
    }

    @Override
    public void removeUserToken(Long userId, String tokenId) {
        stringRedisTemplate.opsForZSet().remove(this.generateUserTokenKey(userId), tokenId);
    }

    @Override
    public Map<String, Long> listUserTokens(Long userId) {
        String key = this.generateUserTokenKey(userId);
        long now = System.currentTimeMillis();
        // 已过期的凭证令牌本身也失效了，不必拉黑，顺手清掉避免索引无限增长
        stringRedisTemplate.opsForZSet().removeRangeByScore(key, 0, now - 1);
        Set<ZSetOperations.TypedTuple<String>> tuples =
                stringRedisTemplate.opsForZSet().rangeByScoreWithScores(key, now, Double.MAX_VALUE);
        Map<String, Long> result = new HashMap<>();
        if (tuples != null) {
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                if (tuple.getValue() != null && tuple.getScore() != null) {
                    result.put(tuple.getValue(), (long) (double) tuple.getScore());
                }
            }
        }
        return result;
    }

    @Override
    public void removeUserTokens(Long userId) {
        stringRedisTemplate.delete(this.generateUserTokenKey(userId));
    }

    /**
     * 生成刷新令牌 redis key
     *
     * @param refreshToken 刷新令牌字符串
     * @return redis key
     */
    private String generateRefreshTokenKey(String refreshToken) {
        return SecurityConst.REDIS_REFRESH_TOKEN_PREFIX + SecurityConst.REDIS_SPLIT + refreshToken;
    }

    /**
     * 生成用户-凭证索引 redis key
     *
     * @param userId 用户id
     * @return redis key
     */
    private String generateUserTokenKey(Long userId) {
        return SecurityConst.REDIS_USER_TOKEN_PREFIX + SecurityConst.REDIS_SPLIT + userId;
    }

}
