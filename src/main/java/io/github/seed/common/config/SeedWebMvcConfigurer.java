package io.github.seed.common.config;

import io.github.seed.common.component.LoginUserArgumentResolver;
import io.github.seed.common.constant.Const;
import io.github.seed.common.filter.ContentCachingRequestFilter;
import io.github.seed.common.filter.ExceptionResolverFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
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
    public void addFormatters(FormatterRegistry registry) {
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

    /**
     * 缓存request的body内容过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<ContentCachingRequestFilter> contentCachingRequestFilterBean() {
        ContentCachingRequestFilter filter = new ContentCachingRequestFilter();
        FilterRegistrationBean<ContentCachingRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(filter.getOrder());
        registration.addUrlPatterns("/*");
        registration.setName("contentCachingRequestFilter");
        log.info("添加缓存Request body过滤器：{}", registration);
        return registration;
    }

    /**
     * 全局异常处理过滤器
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<ExceptionResolverFilter> exceptionResolverFilterBean(HandlerExceptionResolver handlerExceptionResolver) {
        ExceptionResolverFilter filter = new ExceptionResolverFilter(handlerExceptionResolver);
        FilterRegistrationBean<ExceptionResolverFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(filter.getOrder());
        registration.addUrlPatterns("/*");
        registration.setName("exceptionResolverFilter");
        log.info("添加全局异常处理过滤器：{}", registration);
        return registration;
    }

}
