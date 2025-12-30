package io.github.seed.common.config;

import io.github.seed.common.component.DesensitizationJacksonAnnotationIntrospector;
import io.github.seed.common.component.SafeLongSerializer;
import io.github.seed.common.component.StringTrimDeserializer;
import io.github.seed.common.constant.Const;
import io.github.seed.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
     * 自定义Jackson日期时间格式
     *
     * @return
     */
    public SimpleModule javaTimeModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(Const.DATETIME_FORMATTER)));
        module.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(Const.DATE_FORMATTER)));
        module.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(Const.TIME_FORMATTER)));

        // ======================= 时间反序列化规则 ==============================
        module.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(Const.DATETIME_FORMATTER)));
        module.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(Const.DATE_FORMATTER)));
        module.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(Const.TIME_FORMATTER)));
        return module;
    }

    /**
     * 自定义的module
     *
     * @return
     */
    public SimpleModule customerModule() {
        SimpleModule module = new SimpleModule();
        // String类型反序列时自动trim
        module.addDeserializer(String.class, new StringTrimDeserializer());
        // 超过js最大数值范围的long转为字符串
        SafeLongSerializer safeLongSerializer = new SafeLongSerializer();
        module.addSerializer(Long.class, safeLongSerializer);
        module.addSerializer(Long.TYPE, safeLongSerializer);
        return module;
    }

    /**
     * 注册生效时间格式化组件
     *
     * @return
     */
    @Bean
    public JsonMapperBuilderCustomizer jsonMapperBuilderCustomizer() {
        return builder -> builder
                .findAndAddModules()
                .addModule(this.javaTimeModule())
                .addModule(this.customerModule())
                // 添加脱敏拦截器
                .annotationIntrospector(new DesensitizationJacksonAnnotationIntrospector());
    }

    /**
     * 初始化JsonUtils，目的使用相同配置的JsonMapper，防止格式不一致解析失败
     *
     * @param jsonMapper
     * @return
     */
    @Bean
    public ApplicationRunner jsonMapperInitializer(JsonMapper jsonMapper) {
        return args -> JsonUtils.init(jsonMapper);
    }

}
