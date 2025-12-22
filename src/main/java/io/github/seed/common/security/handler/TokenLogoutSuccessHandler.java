package io.github.seed.common.security.handler;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.service.TokenService;
import io.github.seed.common.util.JsonUtils;
import io.github.seed.common.util.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

/**
 * 2023/9/1 Spring Security 注销成功处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class TokenLogoutSuccessHandler implements LogoutSuccessHandler {

    private final TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.debug("TokenLogoutSuccessHandler：{}", authentication);
        String token = SecurityUtils.resolveToken(request);
        if (StrUtil.isNotBlank(token)) {
            tokenService.removeToken(token);
        }
        // 清除SecurityContextHolder上下文
        SecurityContextHolder.clearContext();
        WebUtils.responseJson(response, JsonUtils.EMPTY);
    }

}
