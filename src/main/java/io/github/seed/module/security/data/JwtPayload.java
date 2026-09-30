package io.github.seed.module.security.data;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * JWT载荷数据
 * <br>载荷只是base64url编码而非加密，客户端可直接解出，因此只放鉴权必需字段，
 * 手机号、邮箱、姓名等一概不放，需要时走{@code /auth/user/info}按需查
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
public class JwtPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 账号
     */
    private String username;
    /**
     * 唯一标识，登出与踢人时按它拉黑
     */
    private String jti;
    /**
     * 签发时间，秒
     */
    private Long issuedAt;
    /**
     * 过期时间，秒
     */
    private Long expiresAt;
    /**
     * 角色编码列表，登录那一刻的快照
     * <br>角色对应的权限由节点内存映射展开，所以改权限能即时生效，改用户角色则需重新登录或续签
     */
    private List<String> roles;

}
