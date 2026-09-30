package io.github.seed.module.security.data;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/12/6 刷新令牌
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
public class RefreshToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 刷新令牌
     */
    private String token;
    /**
     * 对应访问令牌的jti，续签时据此把旧访问令牌拉黑
     */
    private String jti;
    /**
     * 剩余有效时间（秒）
     */
    private Long expiresIn;
    /**
     * 签发时间
     */
    private Long issuedAt;
    /**
     * 用户
     */
    private String username;

}
