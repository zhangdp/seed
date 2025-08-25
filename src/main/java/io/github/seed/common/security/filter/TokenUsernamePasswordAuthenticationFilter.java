package io.github.seed.common.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        super(new AntPathRequestMatcher(SecurityConst.LOGIN_URL, "POST"));
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try (InputStream in = request.getInputStream()) {
            PasswordLoginParams params = objectMapper.readValue(in, PasswordLoginParams.class);
            String username = params.getUsername();
            String password = params.getPassword();
            log.trace("TokenUsernamePasswordAuthenticationFilter：{}", username);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(params.getUsername(), password);
            return this.getAuthenticationManager().authenticate(auth);
        }
    }
}
