package com.zhangdp.seed.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.constant.CacheConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
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
        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = this.jsonRedisSerializer();
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        return template;
    }

    /**
     * 自定义redis缓存管理器，设置为json序列化方式
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // todo 这里应改为默认配置取自application.yml配置，在这基础上配置建、值序列化方式
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 过期时间
                .entryTtl(CacheConst.TTL)
                // 前缀
                .prefixCacheNameWith(CacheConst.PREFIX + CacheConst.SPLIT)
                // 键序列化方式 redis字符串序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
                // 值序列化方式 json序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(this.jsonRedisSerializer()));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(config).build();
    }

    /**
     * json格式的序列化方式
     *
     * @return
     */
    public GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 配置[忽略未知字段]
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 配置【空字符串反序列化为对象时为null】
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        // 配置[时间类型转换]
        objectMapper.registerModule(JacksonConfigurer.TIME_MODULE);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

}
