package io.github.seed.module.security.component;

import io.github.seed.module.security.data.RefreshToken;
import io.github.seed.module.security.data.SecurityConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * redis方式保存令牌
 * <br>redis里一律是纯文本：刷新令牌序列化成json字符串，用户-凭证索引的member是jti，
 * 与{@link JtiBlacklist}黑名单里的jti格式保持一致，便于直接查看与排查
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisTokenStore implements TokenStore {

    private final StringRedisTemplate stringRedisTemplate;
    private final JsonMapper jsonMapper;

    @Override
    public void storeRefreshToken(RefreshToken refreshToken, Duration expire) {
        stringRedisTemplate.opsForValue().set(this.generateRefreshTokenKey(refreshToken.getToken()),
                jsonMapper.writeValueAsString(refreshToken), expire);
    }

    @Override
    public RefreshToken loadRefreshToken(String refreshToken) {
        String json = stringRedisTemplate.opsForValue().get(this.generateRefreshTokenKey(refreshToken));
        return json == null ? null : jsonMapper.readValue(json, RefreshToken.class);
    }

    @Override
    public boolean removeRefreshToken(String refreshToken) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(this.generateRefreshTokenKey(refreshToken)));
    }

    @Override
    public void bindRefreshToken(String jti, String refreshToken, Duration expire) {
        stringRedisTemplate.opsForValue().set(this.generateJtiToRefreshKey(jti), refreshToken, expire);
    }

    @Override
    public String takeBoundRefreshToken(String jti) {
        String key = this.generateJtiToRefreshKey(jti);
        String refreshToken = stringRedisTemplate.opsForValue().get(key);
        stringRedisTemplate.delete(key);
        return refreshToken;
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

    /**
     * 生成jti-刷新令牌 redis key
     *
     * @param jti 访问令牌的唯一标识
     * @return redis key
     */
    private String generateJtiToRefreshKey(String jti) {
        return SecurityConst.REDIS_JTI_TO_REFRESH_PREFIX + SecurityConst.REDIS_SPLIT + jti;
    }

}
