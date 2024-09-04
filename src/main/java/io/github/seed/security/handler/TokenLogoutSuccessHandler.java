package io.github.seed.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.common.R;
import io.github.seed.security.SecurityUtils;
import io.github.seed.security.service.TokenService;
import io.github.seed.util.WebUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 2023/9/1 Spring Security 注销成功处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TokenLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("TokenLogoutSuccessHandler：{}", authentication);
        }
        String token = SecurityUtils.resolveToken(request);
        if (StrUtil.isNotBlank(token)) {
            tokenService.removeToken(token);
        }
        // 清除SecurityContextHolder上下文
        SecurityContextHolder.clearContext();
        WebUtils.responseJson(response, objectMapper.writeValueAsString(R.success()));
    }

}
