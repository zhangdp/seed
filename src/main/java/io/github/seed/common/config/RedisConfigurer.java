package io.github.seed.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import io.github.seed.common.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
        RedisSerializer<String> stringRedisSerializer = RedisSerializer.string();
        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = this.jsonRedisSerializer();
        template.setValueSerializer(jsonRedisSerializer);
        template.setHashValueSerializer(jsonRedisSerializer);
        // template.afterPropertiesSet();
        log.info("自定义Redis Key序列化方式：{}, Value序列化方式：{}", "RedisSerializer<String>", jsonRedisSerializer.getClass().getSimpleName());
        return template;
    }

    /**
     * json格式的序列化方式
     *
     * @return
     */
    @Bean
    public GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        jsonRedisSerializer.configure(config -> {
            // 配置jdk8时间格式化
            config.registerModule(JacksonConfigurer.timeModule(Const.DATE_FORMATTER, Const.TIME_FORMATTER, Const.DATETIME_FORMATTER));
            // 配置忽略未知字段
            config.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        });
        return jsonRedisSerializer;
    }

}
