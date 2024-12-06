package io.github.seed.common.security.data;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/6/28 访问令牌
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class AccessToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 令牌
     */
    private String token;
    /**
     * 刷新令牌
     */
    private RefreshToken refreshToken;
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
    private UserDetails userDetails;

}
