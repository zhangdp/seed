package com.zhangdp.seed.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.zhangdp.seed.common.constant.CommonConst;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Instant;
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
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class JacksonConfigurer implements WebMvcConfigurer {

    /**
     * 时间格式化组件
     */
    public final static JavaTimeModule TIME_MODULE;

    static {
        TIME_MODULE = new JavaTimeModule();
        // ======================= 时间序列化规则 ==============================
        TIME_MODULE.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(CommonConst.DATETIME_FORMATTER)));
        TIME_MODULE.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern(CommonConst.DATE_FORMATTER)));
        TIME_MODULE.addSerializer(LocalTime.class,
                new LocalTimeSerializer(DateTimeFormatter.ofPattern(CommonConst.TIME_FORMATTER)));

        // Instant 类型序列化
        TIME_MODULE.addSerializer(Instant.class, InstantSerializer.INSTANCE);

        // ======================= 时间反序列化规则 ==============================
        TIME_MODULE.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(CommonConst.DATETIME_FORMATTER)));
        TIME_MODULE.addDeserializer(LocalDate.class,
                new LocalDateDeserializer(DateTimeFormatter.ofPattern(CommonConst.DATE_FORMATTER)));
        TIME_MODULE.addDeserializer(LocalTime.class,
                new LocalTimeDeserializer(DateTimeFormatter.ofPattern(CommonConst.TIME_FORMATTER)));

        // Instant 反序列化
        TIME_MODULE.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
    }

    /**
     * 注册生效时间格式化组件
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.modules(TIME_MODULE);
    }

    /**
     * 增加GET请求参数中时间类型转换
     * <ul>
     * <li>HH:mm:ss -> LocalTime</li>
     * <li>yyyy-MM-dd -> LocalDate</li>
     * <li>yyyy-MM-dd HH:mm:ss -> LocalDateTime</li>
     * </ul>
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern(CommonConst.TIME_FORMATTER));
        registrar.setDateFormatter(DateTimeFormatter.ofPattern(CommonConst.DATE_FORMATTER));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(CommonConst.DATETIME_FORMATTER));
        registrar.registerFormatters(registry);
    }

}
