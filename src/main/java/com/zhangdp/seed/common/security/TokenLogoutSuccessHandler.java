package com.zhangdp.seed.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.util.WebUtils;
import com.zhangdp.seed.model.dto.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

/**
 * 2023/9/1
 *
 * @author zhangdp
 * @since
 */
@Slf4j
@RequiredArgsConstructor
public class TokenLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("注销处理：{}", authentication);
        }
        UserInfo user = (UserInfo) authentication.getPrincipal();
        WebUtils.responseJson(response, objectMapper.writeValueAsString(R.success()));
    }

}
