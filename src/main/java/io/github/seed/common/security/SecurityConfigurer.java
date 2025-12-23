package io.github.seed.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.common.security.filter.TokenAuthenticationFilter;
import io.github.seed.common.security.filter.TokenAuthenticationProcessingFilter;
import io.github.seed.common.security.handler.*;
import io.github.seed.common.security.service.*;
import io.github.seed.service.sys.ConfigService;
import io.github.seed.service.sys.ResourceService;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * 2023/8/15 spring security 配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigurer {

    /**
     * springsecurity配置
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, TokenAuthenticationFilter tokenAuthenticationFilter,
                                                   // TokenAuthenticationProcessingFilter tokenAuthenticationProcessingFilter,
                                                   LogoutSuccessHandler logoutSuccessHandler, AccessDeniedHandler accessDeniedHandler,
                                                   AuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        httpSecurity
                // 禁用csrf
                .csrf(AbstractHttpConfigurer::disable)
                // 使用无状态session，即不使用session缓存数据
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用httpBasic
                .httpBasic(AbstractHttpConfigurer::disable)
                // 默认表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 登出
                .logout(c -> c.logoutUrl(SecurityConst.LOGOUT_URL).logoutSuccessHandler(logoutSuccessHandler))
                .authorizeHttpRequests(req -> req
                        // 放行url
                        .requestMatchers("/test/**", "/swagger-ui/**", "/v3/**", "/actuator/**",
                                SecurityConst.LOGIN_URL, SecurityConst.LOGOUT_URL).permitAll()
                        // OPTIONS请求放行
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        // 其余url都必须认证
                        .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh
                        // 无权限访问处理器
                        .accessDeniedHandler(accessDeniedHandler)
                        // 未登录处理器
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                // 登录认证处理过滤器
                // .addFilterBefore(tokenAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                // 解析token过滤器
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * 密码加密器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * token持久化
     *
     * @param restTemplate
     * @return
     */
    @Bean
    public TokenStore tokenStore(RedisTemplate<String, Object> restTemplate) {
        return new RedisTokenStore(restTemplate);
    }

    /**
     * token服务
     *
     * @param tokenStore
     * @param configService
     * @return
     */
    @Bean
    public TokenService tokenService(TokenStore tokenStore, ConfigService configService) {
        return new TokenService(tokenStore, configService);
    }

    /**
     * spring security 用户服务
     *
     * @param userService
     * @param roleService
     * @param resourceService
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService(UserService userService, RoleService roleService, ResourceService resourceService) {
        return new DaoUserDetailsService(userService, roleService, resourceService);
    }

    /**
     * 校验token过滤器
     *
     * @param tokenService
     * @return
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(TokenService tokenService) {
        return new TokenAuthenticationFilter(tokenService);
    }

    /**
     * 登录成功处理器
     *
     * @param objectMapper
     * @param tokenService
     * @return
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(ObjectMapper objectMapper, TokenService tokenService) {
        return new TokenAuthenticationSuccessHandler(objectMapper, tokenService);
    }

    /**
     * 访问拒绝处理器
     *
     * @param handlerExceptionResolver
     * @return
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler(HandlerExceptionResolver handlerExceptionResolver) {
        return new TokenAccessDeniedHandler(handlerExceptionResolver);
    }

    /**
     * 认证失败处理器
     *
     * @param handlerExceptionResolver
     * @return
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(HandlerExceptionResolver handlerExceptionResolver) {
        return new TokenAuthenticationEntryPoint(handlerExceptionResolver);
    }

    /**
     * 登录失败处理器
     *
     * @param handlerExceptionResolver
     * @return
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(HandlerExceptionResolver handlerExceptionResolver) {
        return new TokenAuthenticationFailureHandler(handlerExceptionResolver);
    }

    /**
     * 用户密码登录认证器
     *
     * @param passwordEncoder
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * 注销成功处理器
     *
     * @param tokenService
     * @return
     */
    @Bean
    public LogoutSuccessHandler logoutSuccessHandler(TokenService tokenService) {
        return new TokenLogoutSuccessHandler(tokenService);
    }

    /**
     * 短信验证码登录
     *
     * @param userDetailsService
     * @return
     */
    @Bean
    public SmsAuthenticationProvider smsAuthenticationProvider(UserDetailsService userDetailsService) {
        return new SmsAuthenticationProvider(userDetailsService);
    }

    /**
     * 认证管理器
     *
     * @param http
     * @param smsAuthenticationProvider
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, SmsAuthenticationProvider smsAuthenticationProvider) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        // 添加自定义的短信登录
        builder.authenticationProvider(smsAuthenticationProvider);
        return builder.build();
    }

    /**
     * 自定义token登录
     *
     * @param authenticationManager
     * @param authenticationSuccessHandler
     * @param authenticationFailureHandler
     * @param objectMapper
     * @return
     */
    // @Bean
    public TokenAuthenticationProcessingFilter tokenAuthenticationProcessingFilter(AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler, ObjectMapper objectMapper) {
        TokenAuthenticationProcessingFilter filter = new TokenAuthenticationProcessingFilter(objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        //认证成功处理器
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        //认证失败处理器
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return filter;
    }

    /**
     * 认证服务
     *
     * @param authenticationManager
     * @param authenticationSuccessHandler
     * @param authenticationFailureHandler
     * @return
     */
    @Bean
    public SecurityService securityService(AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler) {
        return new SecurityService(authenticationManager, authenticationSuccessHandler, authenticationFailureHandler);
    }

}
