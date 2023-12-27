package com.zhangdp.seed.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
                // 禁用csrf(防止跨站请求伪造攻击)
                // .csrf(AbstractHttpConfigurer::disable)
                // 使用无状态session，即不使用session缓存数据
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> req
                        // 放行url
                        .requestMatchers("/test/**").permitAll()
                        // OPTIONS请求放行
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        // 其余url都必须认证
                        .anyRequest().authenticated())
                .formLogin(login -> login.successHandler(new TokenLoginSuccessHandler(objectMapper)))
                .logout(logout -> logout.logoutSuccessHandler(new TokenLogoutSuccessHandler(objectMapper)));
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

}
