package io.github.seed.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 2024/7/9 redis缓存配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class RedisCacheConfigurer {

    /**
     * 自定义redis缓存配置
     *
     * @param cacheProperties
     * @param genericJackson2JsonRedisSerializer
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties, GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        // 自定义序列化方式
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().
                serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())).
                serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer));
        // 从application.yml读取配置
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        log.info("自定义Redis Cache Key序列化方式：{}，Value序列化方式：{}，配置：{}", "RedisSerializer<String>", genericJackson2JsonRedisSerializer.getClass().getSimpleName(), redisProperties);
        return config;
    }

}
