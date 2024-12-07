package io.github.seed.common.security.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/12/6 刷新令牌
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class RefreshToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 刷新令牌
     */
    private String token;
    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 剩余有效时间（秒）
     */
    private int expiresIn;
    /**
     * 签发时间
     */
    private long issuedAt;
    /**
     * 用户
     */
    private String username;

}
