package io.github.seed.common.config;

import io.github.seed.common.component.DesensitizationJacksonAnnotationIntrospector;
import io.github.seed.common.component.SafeLongSerializer;
import io.github.seed.common.component.StringTrimDeserializer;
import io.github.seed.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;

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
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return builder -> {
            // 自定义Jackson日期时间格式
            builder.addModule(JsonUtils.TIME_MODULE);
            // 添加脱敏拦截器
            builder.annotationIntrospector(new DesensitizationJacksonAnnotationIntrospector());
            SimpleModule module = new SimpleModule();
            // String类型反序列时自动trim
            module.addDeserializer(String.class, new StringTrimDeserializer());
            // 超过js最大数值范围的long转为字符串
            SafeLongSerializer safeLongSerializer = new SafeLongSerializer();
            module.addSerializer(Long.class, safeLongSerializer);
            module.addSerializer(Long.TYPE, safeLongSerializer);
            builder.addModule(module);
        };
    }

}
