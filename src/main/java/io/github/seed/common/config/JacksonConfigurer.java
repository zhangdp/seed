package io.github.seed.common.config;

import io.github.seed.common.component.DesensitizationJacksonAnnotationIntrospector;
import io.github.seed.common.component.StringTrimDeserializer;
import io.github.seed.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson配置，如时间转换等
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@AutoConfigureBefore(JacksonAutoConfiguration.class)
@Slf4j
public class JacksonConfigurer {

    /**
     * 注册生效时间格式化组件
     *
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            // 自定义Jackson日期时间格式
            builder.modulesToInstall(JsonUtils.TIME_MODULE);
            // 添加脱敏拦截器
            builder.annotationIntrospector(new DesensitizationJacksonAnnotationIntrospector());
            // String类型反序列时自动trim
            builder.deserializers(new StringTrimDeserializer());
        };
    }

}
