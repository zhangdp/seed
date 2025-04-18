package io.github.seed.common.security.handler;

import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.UnauthorizedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * 2024/6/28 spring security 认证失败
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.trace("TokenAuthenticationEntryPoint: {}", request.getRequestURI());
        // 转为自定义异常，并委托给异常处理器
        handlerExceptionResolver.resolveException(request, response, null, new UnauthorizedException(ErrorCode.UNAUTHORIZED, authException));
    }
}
