package io.github.seed.module.security.component;

import io.github.seed.module.security.data.JwtPayload;
import io.github.seed.module.security.data.SecurityConst;
import io.github.seed.module.security.data.SecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * JWT签发与解析
 * <br>自包含令牌：校验只依赖签名与本地密钥，不查库也不查redis，代价是无法就地失效，
 * 失效由{@code JtiBlacklist}按jti拉黑兜底
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class JwtTokenProvider {

    /**
     * 载荷里角色列表的key
     */
    private static final String CLAIM_ROLES = "roles";
    /**
     * 载荷里账号的key
     */
    private static final String CLAIM_USERNAME = "username";

    private final SecretKey secretKey;
    private final String issuer;
    private final long clockSkewSeconds;

    public JwtTokenProvider(SecurityProperties properties) {
        SecurityProperties.JwtProperties jwt = properties.getJwt();
        String secret = jwt.getSecret();
        if (secret == null || secret.isBlank()) {
            // 未配置时随机生成：单节点能用，多节点必定互相验签失败
            this.secretKey = Jwts.SIG.HS256.key().build();
            log.warn("未配置{}，已随机生成jwt密钥：{}，多节点部署必须显式配置同一密钥，否则各节点签发的令牌互不认账",
                    SecurityConst.CONFIG_PREFIX + ".jwt.secret",
                    Base64.getEncoder().encodeToString(this.secretKey.getEncoded()));
        } else {
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        }
        this.issuer = jwt.getIssuer();
        this.clockSkewSeconds = jwt.getLeeway() == null ? 0 : jwt.getLeeway().toSeconds();
    }

    /**
     * 签发访问令牌
     *
     * @param jti      令牌唯一标识，由调用方生成并登记，登出与踢人时按它拉黑
     * @param userId   用户id
     * @param username 账号
     * @param roles    角色编码列表，可为空
     * @param ttl      有效期
     * @return 令牌字符串
     */
    public String sign(String jti, Long userId, String username, List<String> roles, Duration ttl) {
        Instant now = Instant.now();
        return Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userId))
                .issuer(this.issuer)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_ROLES, roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                // 显式指定算法：不指定的话jjwt会按密钥长度挑算法，同一套配置换长短密钥就悄悄换了算法
                .signWith(this.secretKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析并校验令牌：验签、校验签发者、校验过期
     *
     * @param token 令牌字符串
     * @return 载荷，令牌无效或已过期时返回null
     */
    public JwtPayload parse(String token) {
        try {
            Jws<Claims> jws = Jwts.parser()
                    .verifyWith(this.secretKey)
                    .requireIssuer(this.issuer)
                    .clockSkewSeconds(this.clockSkewSeconds)
                    .build()
                    .parseSignedClaims(token);
            return this.toPayload(jws.getPayload());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("jwt校验不通过：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 转为载荷对象
     *
     * @param claims 令牌声明
     * @return 载荷
     */
    @SuppressWarnings("unchecked")
    private JwtPayload toPayload(Claims claims) {
        JwtPayload payload = new JwtPayload();
        payload.setUserId(Long.valueOf(claims.getSubject()));
        payload.setUsername(claims.get(CLAIM_USERNAME, String.class));
        payload.setJti(claims.getId());
        payload.setIssuedAt(claims.getIssuedAt() == null ? null : claims.getIssuedAt().toInstant().getEpochSecond());
        payload.setExpiresAt(claims.getExpiration() == null ? null : claims.getExpiration().toInstant().getEpochSecond());
        Object roles = claims.get(CLAIM_ROLES);
        if (roles instanceof List<?> list) {
            payload.setRoles((List<String>) list);
        }
        return payload;
    }

}
