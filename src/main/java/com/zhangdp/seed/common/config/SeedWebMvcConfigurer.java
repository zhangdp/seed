package com.zhangdp.seed.common.config;

import com.zhangdp.seed.common.constant.CommonConst;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;

/**
 * 2023/5/25 mvc配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@Slf4j
public class SeedWebMvcConfigurer implements WebMvcConfigurer {

    /**
     * 增加请求参数中时间类型转换
     * <ul>
     * <li>HH:mm:ss[.SSS] -> LocalTime</li>
     * <li>yyyy-MM-dd -> LocalDate</li>
     * <li>yyyy-MM-dd HH:mm:ss[.SSS] -> LocalDateTime</li>
     * </ul>
     *
     * @param registry
     */
    @Override
    public void addFormatters(@NotNull FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern(CommonConst.TIME_FORMATTER));
        registrar.setDateFormatter(DateTimeFormatter.ofPattern(CommonConst.DATE_FORMATTER));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(CommonConst.DATETIME_FORMATTER));
        registrar.registerFormatters(registry);
    }

}
