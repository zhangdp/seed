package io.github.seed.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 认证相关配置
 *
 * @author zhangdp
 * @since 2026/7/15
 */
@Data
@ConfigurationProperties(value = SecurityConst.CONFIG_PREFIX)
public class SecurityProperties {

    /**
     * 访问令牌有效期
     */
    private Duration accessTokenTtl = Duration.ofMinutes(30);
    /**
     * 刷新令牌有效期
     */
    private Duration refreshTokenTtl = Duration.ofHours(24);
    /**
     * 是否自动刷新访问令牌过期时间
     */
    private boolean autoRenew = false;
    /**
     * 是否启动刷新token
     */
    private boolean enableRefreshToken = true;
    /**
     * 放行的url
     */
    private String[] permitUrls = new String[]{ "/favicon.ico", "/swagger-ui/**", "/v3/**", "/actuator", "/actuator/**", "/error" };
    /**
     * actuator端点认证配置
     */
    private ActuatorProperties actuator;

    /**
     * actuator端点认证配置
     */
    @Data
    public static class ActuatorProperties {

        /**
         * 是否启用
         */
        private boolean enabled = true;
        /**
         * 账号
         */
        private String username = "actuator";
        /**
         * 密码
         */
        private String password = "Seed@2026";
        /**
         * 角色列表
         */
        private String[] roles = new String[]{ "ACTUATOR" };
        /**
         * 放行的url
         */
        private String[] permitUrls = new String[]{ "health", "info" };
    }

}
