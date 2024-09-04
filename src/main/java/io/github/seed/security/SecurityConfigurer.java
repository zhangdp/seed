package io.github.seed.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.security.handler.*;
import io.github.seed.security.service.TokenService;
import io.github.seed.security.filter.TokenAuthenticationFilter;
import io.github.seed.security.filter.TokenUsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

    private final ObjectMapper objectMapper;
    private final TokenAuthenticationSuccessHandler tokenAuthenticationSuccessHandler;
    private final TokenLogoutSuccessHandler tokenLogoutSuccessHandler;
    private final TokenAuthenticationFailureHandler tokenAuthenticationFailureHandler;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;
    private final TokenService tokenService;

    /**
     * springsecurity配置
     *
     * @param httpSecurity
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // 禁用csrf
                .csrf(AbstractHttpConfigurer::disable)
                // 使用无状态session，即不使用session缓存数据
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用httpBasic
                .httpBasic(AbstractHttpConfigurer::disable)
                // 默认账号密码登录
                // .formLogin(AbstractHttpConfigurer::disable)
                // 登出
                .logout(c -> c.logoutUrl(SecurityConst.LOGOUT_URL).logoutSuccessHandler(tokenLogoutSuccessHandler))
                .authorizeHttpRequests(req -> req
                        // 放行url
                        .requestMatchers("/test/**", "/swagger-ui/**", "/v3/**").permitAll()
                        // OPTIONS请求放行
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        // 其余url都必须认证
                        .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh
                        // 无权限访问处理器
                        .accessDeniedHandler(tokenAccessDeniedHandler)
                        // 未登录处理器
                        .authenticationEntryPoint(tokenAuthenticationEntryPoint)
                )
                // 解析token过滤器
                .addFilterBefore(this.tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                // 自定义账号密码登录过滤器
                .addFilterBefore(this.tokenUsernamePasswordAuthenticationFilter(httpSecurity.getSharedObject(AuthenticationManager.class)), UsernamePasswordAuthenticationFilter.class);
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
     * 基于用户名和密码或使用用户名和密码进行身份验证
     *
     * @param provider
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider provider) {
        return new ProviderManager(provider);
    }

    /**
     * 自定义token登录
     *
     * @param authenticationManager
     * @return
     */
    @Bean
    public TokenUsernamePasswordAuthenticationFilter tokenUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        TokenUsernamePasswordAuthenticationFilter filter = new TokenUsernamePasswordAuthenticationFilter(objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        //认证成功处理器
        filter.setAuthenticationSuccessHandler(tokenAuthenticationSuccessHandler);
        //认证失败处理器
        filter.setAuthenticationFailureHandler(tokenAuthenticationFailureHandler);
        return filter;
    }

    /**
     * 校验token过滤器
     *
     * @return
     */
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenService);
    }

}
