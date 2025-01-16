package io.github.seed.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
@Slf4j
public class RedisConfigurer {

    /**
     * json格式的序列化方式
     *
     * @return
     */
    @Bean
    public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer(JavaTimeModule javaTimeModule) {
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        jsonRedisSerializer.configure(config -> {
            // 配置jdk8时间格式化
            config.registerModule(javaTimeModule);
            // 配置忽略未知字段
            config.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 配置时间格式化
            // config.setDateFormat(new SimpleDateFormat(Const.DATETIME_FORMATTER));
        });
        return jsonRedisSerializer;
    }

    /**
     * 设置redistemplate key序列化方式为string，value序列化方式为json
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        template.setValueSerializer(genericJackson2JsonRedisSerializer);
        template.setHashValueSerializer(genericJackson2JsonRedisSerializer);
        // template.afterPropertiesSet();
        log.info("自定义Redis Key序列化方式：{}, Value序列化方式：{}", "RedisSerializer<String>", genericJackson2JsonRedisSerializer.getClass().getSimpleName());
        return template;
    }

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
