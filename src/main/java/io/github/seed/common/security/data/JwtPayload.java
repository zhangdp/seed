package io.github.seed.common.security.data;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 2024/12/2 JWT载荷数据
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Deprecated
@Data
public class JwtPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户标识，此处即账号
     */
    private String sub;
    /**
     * 签发时间
     */
    private Long iat;
    /**
     * 生效时间
     */
    // private Long nbf;
    /**
     * 过期时间
     */
    private Long exp;
    /**
     * 唯一标识
     */
    private String jti;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 角色列表
     */
    private List<String> roles;

}
