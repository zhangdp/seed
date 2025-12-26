package io.github.seed.common.security.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 2025/12/24 Actuator用户配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(value = "management.security.user")
public class ActuatorUserProperties {

    /**
     * 账号
     */
    private String name;
    /**
     * 密码
     */
    private String password;
    /**
     * 角色列表
     */
    private String[] roles;
}
