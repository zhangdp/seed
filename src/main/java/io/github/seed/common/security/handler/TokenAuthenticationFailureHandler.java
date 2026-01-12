package io.github.seed.common.security.handler;

import io.github.seed.common.data.LoginEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * spring security 登录失败处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.debug("TokenAuthenticationFailureHandler:{}", request.getRequestURI());

        // todo 更新密码错误次数等业务

        // 发出登录日志事件，有需要的话订阅
        LoginEvent loginEvent = new LoginEvent(this);
        applicationEventPublisher.publishEvent(loginEvent);
    }
}
