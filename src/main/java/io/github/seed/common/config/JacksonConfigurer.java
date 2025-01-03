package io.github.seed.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.github.seed.common.component.DesensitizationJacksonAnnotationIntrospector;
import io.github.seed.common.constant.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
@ConditionalOnClass(ObjectMapper.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
@Slf4j
public class JacksonConfigurer {

    /**
     * 时间格式化组件
     *
     * @param dateFormatter
     * @param timeFormatter
     * @param dateTimeFormatter
     * @return
     */
    public static JavaTimeModule timeModule(String dateFormatter, String timeFormatter, String dateTimeFormatter) {
        JavaTimeModule timeModule = new JavaTimeModule();
        // ======================= 时间序列化规则 ==============================
        timeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(dateTimeFormatter)));
        timeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(dateFormatter)));
        timeModule.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(timeFormatter)));

        // ======================= 时间反序列化规则 ==============================
        timeModule.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(dateTimeFormatter)));
        timeModule.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(dateFormatter)));
        timeModule.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(timeFormatter)));

        return timeModule;
    }

    /**
     * 注册生效时间格式化组件
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> {
            log.info("自定义Jackson日期时间格式：LocalDate: {}, LocalTime: {}, LocalDateTime: {}", Const.DATE_FORMATTER, Const.TIME_FORMATTER, Const.DATETIME_FORMATTER);
            builder.modules(timeModule(Const.DATE_FORMATTER, Const.TIME_FORMATTER, Const.DATETIME_FORMATTER));
            log.info("添加Jackson自定义脱敏注解处理器");
            builder.annotationIntrospector(new DesensitizationJacksonAnnotationIntrospector());
        };
    }

}
