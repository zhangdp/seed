package io.github.seed.common.security.handler;

import io.github.seed.common.constant.Const;
import io.github.seed.common.data.LoginEvent;
import io.github.seed.common.data.R;
import io.github.seed.common.security.data.RefreshToken;
import io.github.seed.common.security.service.TokenService;
import io.github.seed.common.util.WebUtils;
import io.github.seed.common.security.data.LoginResult;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.common.security.data.AccessToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 2023/9/1 spring security登录成功处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JsonMapper jsonMapper;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("TokenAuthenticationSuccessHandler: {}", authentication);
        LoginUser user = (LoginUser) authentication.getPrincipal();
        // 生成token信息
        AccessToken accessToken = tokenService.createToken(user);
        LoginResult loginResult = new LoginResult();
        loginResult.setAccessToken(accessToken.getToken());
        RefreshToken refreshToken = accessToken.getRefreshToken();
        if (refreshToken != null) {
            loginResult.setRefreshToken(refreshToken.getToken());
        }
        loginResult.setExpiresIn(accessToken.getExpiresIn());
        loginResult.setUserId(user.getId());
        loginResult.setUsername(user.getUsername());
        loginResult.setName(user.getName());

        // response登录结果
        R<LoginResult> r = R.success(loginResult);
        WebUtils.responseJson(response, jsonMapper.writeValueAsString(r), request.getCharacterEncoding());
        // WebUtils.responseJson(response, jsonMapper.writeValueAsString(loginResult), request.getCharacterEncoding());

        // 发出登录日志事件
        this.publishLoginEvent(request, authentication);
    }

    private void publishLoginEvent(HttpServletRequest request, Authentication authentication) {
        LoginEvent event = new LoginEvent(this);
        // event.setLoginType();
        // event.setUsername();
        event.setLoginTime(LocalDateTime.now());
        event.setLoginUser((LoginUser) authentication.getPrincipal());
        event.setClientIp(WebUtils.getClientIP(request));
        event.setUserAgent(request.getHeader("User-Agent"));
        event.setResultCode(Const.RESULT_SUCCESS);
    }

}
