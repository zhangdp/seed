package com.zhangdp.seed.common.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@EnableConfigurationProperties(value = CacheProperties.class)//允许将配置属性配置类放入spring容器中
@Configuration
public class RedisCacheConfigurer {

    /**
     * 自定义redis缓存配置
     *
     * @param cacheProperties
     * @param genericJackson2JsonRedisSerializer
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(ObjectProvider<CacheProperties> cacheProperties, GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        // 自定义序列化方式
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().
                serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())).
                serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericJackson2JsonRedisSerializer));
        // 从application.yml读取配置
        CacheProperties ifAvailable = cacheProperties.getIfAvailable();
        if (null != ifAvailable) {
            CacheProperties.Redis redisProperties = ifAvailable.getRedis();
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
        }
        return config;
    }

}
