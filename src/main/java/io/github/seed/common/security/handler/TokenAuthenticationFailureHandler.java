package io.github.seed.common.security.handler;

import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.exception.ForbiddenException;
import io.github.seed.common.exception.UnauthorizedException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * 2024/6/28 spring security 登录失败处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.trace("TokenAuthenticationFailureHandler:{}", request.getRequestURI());
        Exception ex;
        if (exception instanceof BadCredentialsException) {
            ex = new UnauthorizedException(ErrorCode.USERNAME_NOT_FOUND_OR_BAD_CREDENTIALS, exception);
        } else if (exception instanceof DisabledException) {
            ex = new UnauthorizedException(ErrorCode.ACCOUNT_DISABLED, exception);
        } else if (exception instanceof LockedException) {
            ex = new UnauthorizedException(ErrorCode.ACCOUNT_LOCKED, exception);
        } else {
            ex = new UnauthorizedException(ErrorCode.LOGIN_FAILURE, exception);
        }
        // 转为自定义异常，并委托给异常处理器
        handlerExceptionResolver.resolveException(request, response, null, ex);
    }
}
