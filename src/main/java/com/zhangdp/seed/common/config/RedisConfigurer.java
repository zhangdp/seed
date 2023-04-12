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

import java.lang.reflect.Field;

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
                .entryTtl(CacheConst.ttl)
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
    private GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        // 通过反射获取Mapper对象, 增加一些配置, 增强兼容性
        try {
            Field field = GenericJackson2JsonRedisSerializer.class.getDeclaredField("mapper");
            field.setAccessible(true);
            ObjectMapper objectMapper = (ObjectMapper) field.get(valueSerializer);
            // 配置[忽略未知字段]
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 配置[时间类型转换]
            objectMapper.registerModule(JacksonConfigurer.TIME_MODULE);
        } catch (Exception e) {
            log.warn("配置redis值序列化为json失败", e);
        }
        return valueSerializer;
    }

}
