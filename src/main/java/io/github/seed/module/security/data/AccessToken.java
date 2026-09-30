package io.github.seed.module.security.data;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 访问令牌
 * <br>令牌本身是自包含的jwt，因此这里不再持有用户信息，只留签发结果
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
public class AccessToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 令牌
     */
    private String token;
    /**
     * 令牌唯一标识，登出与踢人时按它拉黑
     */
    private String jti;
    /**
     * 刷新令牌
     */
    private RefreshToken refreshToken;
    /**
     * 剩余有效时间（秒）
     */
    private Long expiresIn;

}
