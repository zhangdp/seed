package com.zhangdp.seed.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.model.params.PasswordLoginParams;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.io.InputStream;

/**
 * 2024/6/27 账号密码登录过滤器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class TokenUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public TokenUsernamePasswordAuthenticationFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher("/auth/login", "POST"));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try (InputStream in = request.getInputStream()) {
            PasswordLoginParams params = objectMapper.readValue(in, PasswordLoginParams.class);
            String username = params.getUsername();
            String password = params.getPassword();
            if (log.isDebugEnabled()) {
                log.debug("账号密码登录过滤器：{}", username);
            }
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(params.getUsername(), password);
            return this.getAuthenticationManager().authenticate(auth);
        }
    }
}
