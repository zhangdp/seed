package io.github.seed.common.security.data;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/6/28 令牌信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class TokenInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 访问令牌剩余有效时间（秒）
     */
    private Integer accessTokenExpiresIn;
    /**
     * 刷新令牌剩余有效时间（秒）
     */
    private Integer refreshTokenExpiresIn;
}
