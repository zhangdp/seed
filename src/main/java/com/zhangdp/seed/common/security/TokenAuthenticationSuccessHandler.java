package com.zhangdp.seed.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.util.WebUtils;
import com.zhangdp.seed.model.dto.LoginResult;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("登录成功处理器：{}", authentication);
        }
        LoginUser user = (LoginUser) authentication.getPrincipal();
        LoginResult loginResult = new LoginResult();
        loginResult.setUser(user);
        loginResult.setUserId(user.getId());
        R<LoginResult> r = R.success(loginResult);
        WebUtils.responseJson(response, objectMapper.writeValueAsString(r));
    }
}
