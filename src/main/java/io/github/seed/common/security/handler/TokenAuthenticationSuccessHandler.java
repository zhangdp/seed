package io.github.seed.common.security.handler;

import cn.hutool.v7.http.server.servlet.ServletUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.common.constant.Const;
import io.github.seed.common.data.LoginLogEvent;
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

    private final ObjectMapper objectMapper;
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
        this.response(response, loginResult);
        // 发出登录日志事件
        this.publishLoginEvent(request, authentication);
    }

    private void publishLoginEvent(HttpServletRequest request, Authentication authentication) {
        LoginLogEvent event = new LoginLogEvent(this);
        // event.setLoginType();
        // event.setUsername();
        event.setLoginTime(LocalDateTime.now());
        event.setLoginUser((LoginUser) authentication.getPrincipal());
        event.setClientIp(ServletUtil.getClientIP(request));
        event.setUserAgent(request.getHeader("User-Agent"));
        event.setResultCode(Const.RESULT_SUCCESS);
    }

    /**
     * 输出结果
     *
     * @param response
     * @param loginResult
     * @throws IOException
     */
    private void response(HttpServletResponse response, LoginResult loginResult) throws IOException {
        // R<LoginResult> r = R.success(loginResult);
        // WebUtils.responseJson(response, objectMapper.writeValueAsString(r));
        WebUtils.responseJson(response, objectMapper.writeValueAsString(loginResult));
    }
}
