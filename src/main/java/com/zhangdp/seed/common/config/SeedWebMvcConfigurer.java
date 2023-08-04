package com.zhangdp.seed.common.config;

import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.component.LoginUserIdArgumentResolver;
import com.zhangdp.seed.common.component.SaTokenHelper;
import com.zhangdp.seed.common.component.SessionArgumentResolver;
import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.filter.SaTokenCheckAuthFilter;
import com.zhangdp.seed.common.filter.SaTokenParameterResolveFilter;
import com.zhangdp.seed.common.util.WebUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
     * actuator端点basic认证账号
     */
    @Value("${actuator.security.user}")
    private String actuatorUser;
    /**
     * actuator端点basic认证密码
     */
    @Value("${actuator.security.password}")
    private String actuatorPassword;

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

    /**
     * 添加参数解析器
     *
     * @param resolvers initially an empty list
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 添加参数解析-当前登录用户id
        resolvers.add(new LoginUserIdArgumentResolver());
        // 添加参数解析-当前登录session
        resolvers.add(new SessionArgumentResolver());
    }

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，打开注解式鉴权功能
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    /**
     * 全局登录检查过滤器
     *
     * @return
     */
    @Bean
    public SaTokenCheckAuthFilter saTokenCheckAuthFilter(ObjectMapper objectMapper) {
        return new SaTokenCheckAuthFilter()
                // 拦截全部请求
                .includePaths("/**")
                // 不需要检查登录的请求
                .excludePaths("/favicon.ico", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/druid/**",
                        "/actuator/health", "/actuator/info", "/auth/login", "/auth/logout", "/test/**")
                // 检查策略
                .check(request -> {
                    // actuator端点检查，需要basic认证
                    SaRouter.match("/actuator/**")
                            .check(o -> SaBasicUtil.check(this.actuatorUser + ":" + this.actuatorPassword))
                            .stop();
                    // 其余全部路径需要登录检查
                    SaRouter.match("/**").check(o -> {
                        // 检查是否登录
                        StpUtil.checkLogin();
                        // 检查成功后续签token有效期
                        StpUtil.updateLastActivityToNow();
                    });
                })
                // 失败策略
                .error((request, response, t) -> {
                    log.warn("SaTokenCheckAuthFilter 失败: uri={}, error={}", request.getRequestURI(), t.getLocalizedMessage());
                    // 统一响应401状态码
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    /* Sa-Token 框架已自动添加此header
                    // Basic认证未通过时需输出要求Basic的响应头
                    if (t instanceof NotBasicAuthException) {
                        response.addHeader("WWW-Authenticate", "Basic");
                    }
                    */
                    R<?> r;
                    // 区分具体检查失败原因如是因为token不存在还是token已过期等
                    if (t instanceof SaTokenException) {
                        r = SaTokenHelper.resolveError((SaTokenException) t);
                    } else {
                        r = new R<>(ErrorCode.UNAUTHORIZED);
                    }
                    WebUtils.responseJson(response, objectMapper.writeValueAsString(r));
                });
    }

    /**
     * 从参数中解析token过滤器
     *
     * @param saTokenConfig
     * @return
     */
    @Bean
    public SaTokenParameterResolveFilter saTokenParameterResolveFilter(SaTokenConfig saTokenConfig) {
        return new SaTokenParameterResolveFilter(saTokenConfig);
    }

}
