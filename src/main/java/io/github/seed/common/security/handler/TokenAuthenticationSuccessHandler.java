package io.github.seed.common.security.handler;

import io.github.seed.common.constant.Const;
import io.github.seed.common.data.LoginEvent;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.common.util.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * spring security登录成功处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("TokenAuthenticationSuccessHandler: {}", authentication);

        // todo 修改上次登录时间、地点等业务

        // 发出登录日志事件，有需要的话订阅
        LoginEvent event = new LoginEvent(this);
        // event.setLoginType();
        // event.setUsername();
        event.setLoginTime(LocalDateTime.now());
        event.setLoginUser((LoginUser) authentication.getPrincipal());
        event.setClientIp(WebUtils.getClientIP(request));
        event.setUserAgent(request.getHeader("User-Agent"));
        event.setResultCode(Const.RESULT_SUCCESS);
        applicationEventPublisher.publishEvent(event);
    }

}
