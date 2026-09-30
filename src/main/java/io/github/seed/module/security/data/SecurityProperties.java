package io.github.seed.module.security.data;

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
     * 是否启动刷新token
     */
    private boolean enableRefreshToken = true;
    /**
     * jwt配置
     */
    private JwtProperties jwt = new JwtProperties();
    /**
     * 放行的url
     */
    private String[] permitUrls = new String[]{ "/favicon.ico", "/swagger-ui/**", "/v3/**", "/error", "/actuator/**" };
    /**
     * actuator端点认证配置
     */
    private ActuatorProperties actuator;

    /**
     * jwt配置
     */
    @Data
    public static class JwtProperties {

        /**
         * 签名密钥，HS256要求至少32字节
         * <br>未配置时启动随机生成一个：单节点能跑，多节点会因密钥不同互相验签失败，必须显式配置
         */
        private String secret;
        /**
         * 签发者
         */
        private String issuer = "seed";
        /**
         * 允许的时钟偏移，节点间时钟不一致时靠它兜底
         */
        private Duration leeway = Duration.ofSeconds(30);
    }

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
