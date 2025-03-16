package io.github.seed.common.config;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.github.seed.common.component.DesensitizationJacksonAnnotationIntrospector;
import io.github.seed.common.component.StringTrimDeserializer;
import io.github.seed.common.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     * jackson jdk8时间格式化模块
     *
     * @return
     */
    @Bean
    public JavaTimeModule javaTimeModule() {
        JavaTimeModule timeModule = new JavaTimeModule();
        // ======================= 时间序列化规则 ==============================
        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(Const.DATETIME_FORMATTER)));
        timeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(Const.DATE_FORMATTER)));
        timeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(Const.TIME_FORMATTER)));

        // ======================= 时间反序列化规则 ==============================
        timeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(Const.DATETIME_FORMATTER)));
        timeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(Const.DATE_FORMATTER)));
        timeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(Const.TIME_FORMATTER)));

        return timeModule;
    }


    /**
     * 注册生效时间格式化组件
     *
     * @return
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer(JavaTimeModule javaTimeModule) {
        return builder -> {
            // 自定义Jackson日期时间格式
            builder.modulesToInstall(javaTimeModule);
            // 添加脱敏拦截器
            builder.annotationIntrospector(new DesensitizationJacksonAnnotationIntrospector());
            // String类型反序列时自动trim
            builder.deserializers(new StringTrimDeserializer());
        };
    }

}
