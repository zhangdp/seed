package io.github.seed.common.security;

import io.github.seed.common.component.IgnoreAuthPathRegistry;
import io.github.seed.common.security.data.ActuatorUserProperties;
import io.github.seed.common.security.filter.TokenResolveAuthenticationFilter;
import io.github.seed.common.security.handler.*;
import io.github.seed.common.security.service.*;
import io.github.seed.service.sys.ConfigService;
import io.github.seed.service.sys.PermissionService;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Set;

/**
 * spring security 配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ActuatorUserProperties.class)
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigurer {

    private final ActuatorUserProperties actuatorUserProperties;

    /**
     * 单独配置/actuator端点的认证
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    @Order(1)
    SecurityFilterChain actuatorSecurity(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        http
                .securityMatcher(EndpointRequest.toAnyEndpoint())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                        .anyRequest().hasRole("ACTUATOR")
                )
                .authenticationManager(new ProviderManager(
                        List.of(new DaoAuthenticationProvider(actuatorUserDetailsService(passwordEncoder)) {{
                            setPasswordEncoder(passwordEncoder);
                        }})
                ))
                // 使用httpBasic认证
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * actuator端点用户
     *
     * @return
     */
    private UserDetailsService actuatorUserDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails userDetails = User.withUsername(this.actuatorUserProperties.getName())
                .password(passwordEncoder.encode(this.actuatorUserProperties.getPassword()))
                .roles(this.actuatorUserProperties.getRoles()).build();
        log.info("actuator端点专用用户：{}", userDetails);
        return new InMemoryUserDetailsManager(userDetails);
    }

    /**
     * 业务spring security配置
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, TokenResolveAuthenticationFilter tokenResolveAuthenticationFilter,
                                                   // TokenAuthenticationProcessingFilter tokenAuthenticationProcessingFilter,
                                                   // LogoutSuccessHandler logoutSuccessHandler,
                                                   AccessDeniedHandler accessDeniedHandler, AuthenticationEntryPoint authenticationEntryPoint,
                                                   IgnoreAuthPathRegistry ignoreAuthPathRegistry) throws Exception {
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
                // .logout(c -> c.logoutUrl(SecurityConst.LOGOUT_URL).logoutSuccessHandler(logoutSuccessHandler))
                .authorizeHttpRequests(req -> {
                            // 动态添加带有@IgnoreAuth的接口
                            Set<String> ignorePaths = ignoreAuthPathRegistry.getIgnoreAuthPaths();
                            if (ignorePaths != null && !ignorePaths.isEmpty()) {
                                for (String ignorePath : ignorePaths) {
                                    req.requestMatchers(ignorePath).permitAll();
                                    log.info("添加忽略认证url：{}", ignorePath);
                                }
                            }
                            // 固定放行url
                            if (SecurityConst.PERMIT_URLS != null && SecurityConst.PERMIT_URLS.length > 0) {
                                for (String permitUrl : SecurityConst.PERMIT_URLS) {
                                    req.requestMatchers(permitUrl).permitAll();
                                    log.info("添加忽略认证url：{}", permitUrl);
                                }
                            }
                            // OPTIONS请求放行
                            req.requestMatchers(HttpMethod.OPTIONS).permitAll();
                            // 其余url都必须认证
                            req.anyRequest().authenticated();
                        }
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
                .addFilterBefore(tokenResolveAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    /**
     * 动态忽略认证的url注册器
     *
     * @param requestMappingHandlerMapping
     * @return
     */
    @Bean
    public IgnoreAuthPathRegistry ignoreAuthPathRegistry(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new IgnoreAuthPathRegistry(requestMappingHandlerMapping);
    }

    /**
     * 密码加密器
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        log.info("Security密码加密器：{}", encoder);
        return encoder;
    }

    /**
     * token持久化
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public TokenStore tokenStore(RedisTemplate<String, Object> redisTemplate) {
        RedisTokenStore store = new RedisTokenStore(redisTemplate);
        log.info("Security token访问使用redis：{}", store);
        return store;
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
     * @param permissionService
     * @return
     */
    @Bean
    public UserDetailsService daoUserDetailsService(UserService userService, RoleService roleService, PermissionService permissionService) {
        return new DaoUserDetailsService(userService, roleService, permissionService);
    }

    /**
     * 校验token过滤器
     *
     * @param tokenService
     * @return
     */
    @Bean
    public TokenResolveAuthenticationFilter tokenAuthenticationFilter(TokenService tokenService) {
        return new TokenResolveAuthenticationFilter(tokenService);
    }

    /**
     * 登录成功处理器
     *
     * @param applicationEventPublisher
     * @return
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(ApplicationEventPublisher applicationEventPublisher) {
        return new TokenAuthenticationSuccessHandler(applicationEventPublisher);
    }

    /**
     * 登录失败处理器
     *
     * @param applicationEventPublisher
     * @return
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(ApplicationEventPublisher applicationEventPublisher) {
        return new TokenAuthenticationFailureHandler(applicationEventPublisher);
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
     * 用户密码登录认证器
     *
     * @param passwordEncoder
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
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
     * 刷新令牌登录
     *
     * @param userDetailsService
     * @param tokenService
     * @return
     */
    @Bean
    public RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider(UserDetailsService userDetailsService, TokenService tokenService) {
        return new RefreshTokenAuthenticationProvider(userDetailsService, tokenService);
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
    public AuthenticationManager authenticationManager(HttpSecurity http, SmsAuthenticationProvider smsAuthenticationProvider, RefreshTokenAuthenticationProvider refreshTokenAuthenticationProvider) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        // 添加自定义的短信登录
        builder.authenticationProvider(smsAuthenticationProvider);
        // 添加刷新令牌登录
        builder.authenticationProvider(refreshTokenAuthenticationProvider);
        return builder.build();
    }

    /**
     * 认证服务
     *
     * @param authenticationManager
     * @param authenticationSuccessHandler
     * @param authenticationFailureHandler
     * @param tokenService
     * @return
     */
    @Bean
    public SecurityService securityService(AuthenticationManager authenticationManager, AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationFailureHandler authenticationFailureHandler, TokenService tokenService) {
        return new SecurityService(authenticationManager, authenticationSuccessHandler, authenticationFailureHandler, tokenService);
    }

}
