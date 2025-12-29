package io.github.seed.common.data;

import io.github.seed.common.enums.LoginType;
import io.github.seed.common.security.data.LoginUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 2025/12/23 登录日志事件
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@ToString
public class LoginLogEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录类型
     */
    @Setter
    private LoginType loginType;
    /**
     * 账号
     */
    @Setter
    private String username;
    /**
     * 登录时间
     */
    @Setter
    private LocalDateTime loginTime;
    /**
     * 登录的用户
     */
    @Setter
    private LoginUser loginUser;
    /**
     * 客户端ip
     */
    @Setter
    private String clientIp;
    /**
     * 浏览器标识
     */
    @Setter
    private String userAgent;
    /**
     * 结果标识
     */
    @Setter
    private Integer resultCode;
    /**
     * 异常
     */
    @Setter
    private Throwable throwable;

    public LoginLogEvent(Object source) {
        super(source);
    }
}
