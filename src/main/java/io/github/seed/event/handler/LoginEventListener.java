package io.github.seed.event.handler;

import io.github.seed.common.constant.Const;
import io.github.seed.entity.sys.LoginLog;
import io.github.seed.event.data.LoginEvent;
import io.github.seed.module.security.data.LoginUser;
import io.github.seed.service.sys.LoginLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 登录日志监听器：认证成功/失败处理器发出{@link LoginEvent}后由这里落库
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginEventListener {

    private final LoginLogService loginLogService;

    /**
     * 监听登录事件，落库登录日志
     *
     * @param event 登录事件
     */
    @EventListener(LoginEvent.class)
    public void onEvent(LoginEvent event) {
        try {
            log.debug("收到LoginEvent: {}", event);
            LoginUser loginUser = event.getLoginUser();
            LoginLog loginLog = new LoginLog();
            // 登录失败时没有用户信息
            loginLog.setUserId(loginUser == null ? null : loginUser.getId());
            loginLog.setUsername(event.getUsername());
            loginLog.setType(event.getLoginType() == null ? null : event.getLoginType().type());
            loginLog.setLoginAt(event.getLoginTime() == null ? LocalDateTime.now() : event.getLoginTime());
            loginLog.setResultCode(event.getResultCode() == null ? Const.RESULT_FAIL : event.getResultCode());
            loginLog.setClientIp(event.getClientIp());
            // user_agent列最长512，超长直接截断，避免整条日志写入失败
            String userAgent = event.getUserAgent();
            loginLog.setUserAgent(userAgent != null && userAgent.length() > 512 ? userAgent.substring(0, 512) : userAgent);
            // todo location：接入ip归属地库后按clientIp解析填充
            this.loginLogService.insert(loginLog);
        } catch (Exception e) {
            log.error("登录日志事件处理失败，event: {}", event, e);
        }
    }

}
