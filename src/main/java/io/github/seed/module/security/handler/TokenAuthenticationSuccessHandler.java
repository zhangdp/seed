package io.github.seed.module.security.handler;

import io.github.seed.common.constant.Const;
import io.github.seed.event.data.LoginEvent;
import io.github.seed.module.security.component.SecurityUtils;
import io.github.seed.module.security.data.LoginUser;
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

        // 发出登录日志事件，由LoginEventListener入库
        LoginEvent event = new LoginEvent(this);
        // 登录类型与账号都取提交的原始认证对象：认证后的对象类型不准（续签返回的是短信登录token）
        Authentication submit = SecurityUtils.getLoginAuthentication(request);
        if (submit == null) {
            submit = authentication;
        }
        LoginUser loginUser = authentication.getPrincipal() instanceof LoginUser principal ? principal : null;
        event.setLoginType(SecurityUtils.resolveLoginType(submit));
        // 续签提交的认证对象里是refresh token明文，取不到账号，回退到认证后的用户
        String username = SecurityUtils.resolveUsername(submit);
        event.setUsername(username != null ? username : (loginUser == null ? null : loginUser.getUsername()));
        event.setLoginTime(LocalDateTime.now());
        event.setLoginUser(loginUser);
        event.setClientIp(WebUtils.getClientIP(request));
        event.setUserAgent(request.getHeader("User-Agent"));
        event.setResultCode(Const.RESULT_SUCCESS);
        applicationEventPublisher.publishEvent(event);
    }

}
