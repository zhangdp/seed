package io.github.seed.common.data;

import io.github.seed.common.enums.LoginType;
import io.github.seed.common.security.data.LoginUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 登录事件
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LoginEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录类型
     */
    private LoginType loginType;
    /**
     * 账号
     */
    private String username;
    /**
     * 登录时间
     */
    private LocalDateTime loginTime;
    /**
     * 登录的用户
     */
    private LoginUser loginUser;
    /**
     * 客户端ip
     */
    private String clientIp;
    /**
     * 浏览器标识
     */
    private String userAgent;
    /**
     * 结果标识
     */
    private Integer resultCode;
    /**
     * 异常
     */
    private Throwable throwable;

    public LoginEvent(Object source) {
        super(source);
    }
}
