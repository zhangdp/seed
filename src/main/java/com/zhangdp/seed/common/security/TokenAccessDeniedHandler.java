package com.zhangdp.seed.common.security;

import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.ForbiddenException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * 2024/6/28 spring security 访问拒绝处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("TokenAccessDeniedHandler: {}", request.getRequestURI());
        }
        // 包装成自定义异常并委托给异常处理器
        handlerExceptionResolver.resolveException(request, response, null, new ForbiddenException(ErrorCode.FORBIDDEN, accessDeniedException));
    }
}
