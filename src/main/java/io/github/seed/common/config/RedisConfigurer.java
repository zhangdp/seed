package io.github.seed.common.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.cache.autoconfigure.CacheAutoConfiguration;
import org.springframework.boot.cache.autoconfigure.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * 2023/4/7 redis配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@AutoConfigureAfter({JacksonConfigurer.class})
@AutoConfigureBefore({CacheAutoConfiguration.class})
@Slf4j
public class RedisConfigurer {

    /**
     * 设置redistemplate key序列化方式为string，value序列化方式为json
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        RedisSerializer<Object> jsonValueSerializer = RedisSerializer.json();
        template.setValueSerializer(jsonValueSerializer);
        template.setHashValueSerializer(jsonValueSerializer);
        template.afterPropertiesSet();
        log.info("自定义Redis Key序列化方式：{}, Value序列化方式：{}", "RedisSerializer<String>", jsonValueSerializer.getClass().getSimpleName());
        return template;
    }

    /**
     * 自定义redis缓存配置
     *
     * @param cacheProperties
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        RedisSerializer<Object> jsonValueSerializer = RedisSerializer.json();
        // 自定义序列化方式
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonValueSerializer));
        // 从application.yml读取配置
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        // 很奇怪，源码每次设置都是new一个新对象出来，所以config需要指向最新的
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
        log.info("自定义Redis Cache Key序列化方式：{}，Value序列化方式：{}，配置：{}", "RedisSerializer<String>", jsonValueSerializer.getClass().getSimpleName(), ToStringBuilder.reflectionToString(redisProperties));
        return config;
    }

}
