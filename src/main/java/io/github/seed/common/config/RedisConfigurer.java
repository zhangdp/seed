package io.github.seed.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.cache.autoconfigure.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.io.Serial;

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
    public GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer() {
        return GenericJacksonJsonRedisSerializer.builder().customize(builder -> {
            // 反序列化时类名验证器：全允许
            PolymorphicTypeValidator validator = new PolymorphicTypeValidator.Base() {
                @Serial
                private static final long serialVersionUID = 1L;

                @Override
                public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
                    return Validity.INDETERMINATE;
                }

                @Override
                public Validity validateSubClassName(DatabindContext ctxt, JavaType baseType, String subClassName) {
                    return Validity.ALLOWED;
                }

                @Override
                public Validity validateSubType(DatabindContext ctxt, JavaType baseType, JavaType subType) {
                    return Validity.ALLOWED;
                }
            };
            builder
                    // 自定义Date格式化
                    // .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"))
                    // 配置忽略未知字段
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    // 序列化时包含全限定类名，方便反序列化时还原真实对象类型。范围：非 final 的类型，位置：属性@class
                    .activateDefaultTyping(validator, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }).build();
    }

    /**
     * 设置redistemplate key序列化方式为string，value序列化方式为json
     *
     * @param connectionFactory
     * @param genericJacksonJsonRedisSerializer
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        template.setValueSerializer(genericJacksonJsonRedisSerializer);
        template.setHashValueSerializer(genericJacksonJsonRedisSerializer);
        template.afterPropertiesSet();
        log.info("自定义Redis Key序列化方式：{}, Value序列化方式：{}", "RedisSerializer<String>", genericJacksonJsonRedisSerializer.getClass().getSimpleName());
        return template;
    }

    /**
     * 自定义redis缓存配置
     *
     * @param cacheProperties
     * @param genericJacksonJsonRedisSerializer
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties, GenericJacksonJsonRedisSerializer genericJacksonJsonRedisSerializer) {
        // 自定义序列化方式
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig().serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string())).serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(genericJacksonJsonRedisSerializer));
        // 从application.yml读取配置
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        if (redisProperties.getTimeToLive() != null) {
            config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config.disableKeyPrefix();
        }
        log.info("自定义Redis Cache Key序列化方式：{}，Value序列化方式：{}，配置：{}", "RedisSerializer<String>", genericJacksonJsonRedisSerializer.getClass().getSimpleName(), redisProperties);
        return config;
    }

}
