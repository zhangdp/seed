package io.github.seed.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.common.security.data.SmsAuthenticationToken;
import io.github.seed.model.params.LoginParams;
import io.github.seed.model.params.PasswordLoginParams;
import io.github.seed.common.security.SecurityConst;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * 2024/6/27 登录处理过滤器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class TokenAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    public TokenAuthenticationProcessingFilter(ObjectMapper objectMapper) {
        super(new AntPathRequestMatcher(SecurityConst.LOGIN_URL, "POST"));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.debug("TokenAuthenticationProcessingFilter: {}", request.getRequestURI());
        try (BufferedReader reader = request.getReader()) {
            LoginParams loginParams = objectMapper.readValue(reader, LoginParams.class);
            Authentication authentication = switch (loginParams.getLoginType()) {
                case PASSWORD ->
                        new UsernamePasswordAuthenticationToken(loginParams.getUsername(), loginParams.getPassword());
                case SMS -> new SmsAuthenticationToken(loginParams.getUsername(), loginParams.getCode());
                default -> throw new IllegalArgumentException("不支持的登录方式：" + loginParams.getLoginType());
            };
            return super.getAuthenticationManager().authenticate(authentication);
        }
    }
}
