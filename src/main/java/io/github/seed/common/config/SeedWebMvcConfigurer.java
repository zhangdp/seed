package io.github.seed.common.config;

import io.github.seed.common.component.LoginUserArgumentResolver;
import io.github.seed.common.constant.Const;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
        registrar.setTimeFormatter(DateTimeFormatter.ofPattern(Const.TIME_FORMATTER));
        registrar.setDateFormatter(DateTimeFormatter.ofPattern(Const.DATE_FORMATTER));
        registrar.setDateTimeFormatter(DateTimeFormatter.ofPattern(Const.DATETIME_FORMATTER));
        registrar.registerFormatters(registry);
    }

    /**
     * 添加自定义controller参数注入
     *
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 添加参数注入-当前登录用户
        resolvers.add(new LoginUserArgumentResolver());
    }

}
