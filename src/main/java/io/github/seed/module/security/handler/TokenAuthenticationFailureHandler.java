package io.github.seed.module.security.handler;

import io.github.seed.common.constant.Const;
import io.github.seed.event.data.LoginEvent;
import io.github.seed.module.security.component.SecurityUtils;
import io.github.seed.common.util.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.time.LocalDateTime;

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

        // 发出登录日志事件，由LoginEventListener入库
        LoginEvent loginEvent = new LoginEvent(this);
        // 认证异常不携带提交的authentication，只能取请求中暂存的原始认证对象
        Authentication submit = SecurityUtils.getLoginAuthentication(request);
        loginEvent.setLoginType(SecurityUtils.resolveLoginType(submit));
        loginEvent.setUsername(SecurityUtils.resolveUsername(submit));
        loginEvent.setLoginTime(LocalDateTime.now());
        loginEvent.setClientIp(WebUtils.getClientIP(request));
        loginEvent.setUserAgent(request.getHeader("User-Agent"));
        loginEvent.setResultCode(Const.RESULT_FAIL);
        loginEvent.setThrowable(exception);
        applicationEventPublisher.publishEvent(loginEvent);
    }
}
