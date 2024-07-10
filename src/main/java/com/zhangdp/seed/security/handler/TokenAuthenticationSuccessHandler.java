package com.zhangdp.seed.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.security.service.TokenService;
import com.zhangdp.seed.util.WebUtils;
import com.zhangdp.seed.security.data.LoginResult;
import com.zhangdp.seed.security.data.LoginUser;
import com.zhangdp.seed.security.data.TokenInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 2023/9/1 spring security登录成功处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TokenAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("TokenAuthenticationSuccessHandler: {}", authentication);
        }
        LoginUser user = (LoginUser) authentication.getPrincipal();
        // 生成token信息
        TokenInfo tokenInfo = tokenService.createToken(user);
        LoginResult loginResult = new LoginResult();
        loginResult.setAccessToken(tokenInfo.getAccessToken());
        loginResult.setRefreshToken(tokenInfo.getRefreshToken());
        loginResult.setExpiresIn(tokenInfo.getAccessTokenExpiresIn());
        loginResult.setUserId(user.getId());
        loginResult.setUsername(user.getUsername());
        loginResult.setName(user.getName());
        this.response(response, loginResult);
    }

    /**
     * 输出结果
     *
     * @param response
     * @param loginResult
     * @throws IOException
     */
    private void response(HttpServletResponse response, LoginResult loginResult) throws IOException {
        R<LoginResult> r = R.success(loginResult);
        WebUtils.responseJson(response, objectMapper.writeValueAsString(r));
    }
}
