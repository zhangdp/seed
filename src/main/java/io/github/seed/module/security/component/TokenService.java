package io.github.seed.module.security.component;

import cn.hutool.v7.core.data.id.IdUtil;
import io.github.seed.module.security.data.AccessToken;
import io.github.seed.module.security.data.JwtPayload;
import io.github.seed.module.security.data.LoginUser;
import io.github.seed.module.security.data.RefreshToken;
import io.github.seed.module.security.data.RolePermissionGrantedAuthority;
import io.github.seed.module.security.data.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * 令牌服务
 * <br>访问令牌是自包含的jwt：签发后不再落库，校验只靠签名，因此每次请求无需查redis；
 * 代价是无法就地修改，作废只能靠{@code JtiBlacklist}按jti拉黑
 * <br>刷新令牌仍需可吊销，留在redis
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final TokenStore tokenStore;
    private final SecurityProperties securityProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final JtiBlacklist jtiBlacklist;
    private final RolePermissionProvider rolePermissionProvider;

    /**
     * 创建令牌
     *
     * @param userDetails 登录用户，需带上id、账号与角色
     * @return 访问令牌与刷新令牌
     */
    public AccessToken createToken(UserDetails userDetails) {
        Duration ttl = securityProperties.getAccessTokenTtl();
        long now = System.currentTimeMillis();
        String jti = IdUtil.fastUUID();
        String token = this.jwtTokenProvider.sign(jti, this.resolveUserId(userDetails), userDetails.getUsername(),
                this.resolveRoleCodes(userDetails), ttl);
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setJti(jti);
        accessToken.setExpiresIn(ttl.toSeconds());
        // 登记到用户-凭证索引，改密码、禁用、踢人时据此一次性作废
        this.tokenStore.registerUserToken(this.resolveUserId(userDetails), jti, now + ttl.toMillis());
        if (securityProperties.isEnableRefreshToken()) {
            RefreshToken refreshToken = this.createRefreshToken(jti, userDetails);
            accessToken.setRefreshToken(refreshToken);
            this.tokenStore.storeRefreshToken(refreshToken, Duration.ofSeconds(refreshToken.getExpiresIn()));
            this.tokenStore.registerUserToken(this.resolveUserId(userDetails), refreshToken.getToken(),
                    now + refreshToken.getExpiresIn() * 1000);
            this.tokenStore.bindRefreshToken(jti, refreshToken.getToken(), Duration.ofSeconds(refreshToken.getExpiresIn()));
        }
        return accessToken;
    }

    /**
     * 生成刷新令牌
     *
     * @param jti         访问令牌的唯一标识，刷新时据此把旧令牌拉黑
     * @param userDetails 登录用户
     * @return 刷新令牌
     */
    private RefreshToken createRefreshToken(String jti, UserDetails userDetails) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(IdUtil.fastUUID());
        refreshToken.setJti(jti);
        refreshToken.setIssuedAt(System.currentTimeMillis());
        refreshToken.setExpiresIn(securityProperties.getRefreshTokenTtl().toSeconds());
        refreshToken.setUsername(userDetails.getUsername());
        return refreshToken;
    }

    /**
     * 解析访问令牌得到登录用户：验签、查黑名单、用内存的角色权限映射还原授权
     *
     * @param accessToken 访问令牌字符串
     * @return 登录用户，令牌无效、已过期或已被拉黑时返回null
     */
    public LoginUser loadLoginUser(String accessToken) {
        JwtPayload payload = this.jwtTokenProvider.parse(accessToken);
        if (payload == null || payload.getJti() == null) {
            return null;
        }
        if (this.jtiBlacklist.isBlacklisted(payload.getJti())) {
            log.debug("访问令牌已被拉黑，jti={}", payload.getJti());
            return null;
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setId(payload.getUserId());
        loginUser.setUsername(payload.getUsername());
        loginUser.setAuthorities(this.rolePermissionProvider.listAuthorities(payload.getRoles()));
        return loginUser;
    }

    /**
     * 加载刷新令牌，已被拉黑的一并返回null
     *
     * @param refreshToken 刷新令牌字符串
     * @return 刷新令牌，不存在或已作废时返回null
     */
    public RefreshToken loadRefreshToken(String refreshToken) {
        if (this.jtiBlacklist.isBlacklisted(refreshToken)) {
            return null;
        }
        return this.tokenStore.loadRefreshToken(refreshToken);
    }

    /**
     * 作废旧令牌：刷新令牌换新时调用，旧访问令牌与旧刷新令牌一起失效
     *
     * @param refreshToken 刷新令牌
     */
    public void removeToken(RefreshToken refreshToken) {
        this.tokenStore.removeRefreshToken(refreshToken.getToken());
        this.jtiBlacklist.blacklist(refreshToken.getToken(), refreshToken.getIssuedAt() + refreshToken.getExpiresIn() * 1000);
        if (refreshToken.getJti() != null) {
            // 旧访问令牌按令牌有效期拉黑即可，过期后自然没人认，不必永久记着
            this.jtiBlacklist.blacklist(refreshToken.getJti(),
                    System.currentTimeMillis() + securityProperties.getAccessTokenTtl().toMillis());
            // 旧访问令牌已作废，它指向旧刷新令牌的映射也一并解绑，不然后续续签会攒下一堆没人用的key
            this.tokenStore.takeBoundRefreshToken(refreshToken.getJti());
        }
    }

    /**
     * 登出：撤销当前访问令牌及其绑定的刷新令牌
     * <br>访问令牌是自包含的jwt，只带得出jti，刷新令牌靠{@code jti->刷新令牌}映射反查，
     * 因此调用方不必再提供刷新令牌；这里是全流程唯一一处为登出查redis的地方，请求鉴权不受影响
     *
     * @param accessToken 访问令牌字符串
     * @return 是否撤销成功，令牌本身无效时返回false
     */
    public boolean removeToken(String accessToken) {
        JwtPayload payload = this.jwtTokenProvider.parse(accessToken);
        if (payload == null || payload.getJti() == null) {
            return false;
        }
        this.jtiBlacklist.blacklist(payload.getJti(), payload.getExpiresAt() * 1000);
        this.tokenStore.removeUserToken(payload.getUserId(), payload.getJti());
        String refreshToken = this.tokenStore.takeBoundRefreshToken(payload.getJti());
        if (refreshToken != null) {
            this.tokenStore.removeRefreshToken(refreshToken);
            this.jtiBlacklist.blacklist(refreshToken, System.currentTimeMillis() + securityProperties.getRefreshTokenTtl().toMillis());
            if (payload.getUserId() != null) {
                this.tokenStore.removeUserToken(payload.getUserId(), refreshToken);
            }
        }
        return true;
    }

    /**
     * 把某个用户已签发的令牌全部作废：改密码、禁用账号、管理员踢人
     *
     * @param userId 用户id
     * @return 作废的凭证数量
     */
    public int removeUserTokens(Long userId) {
        Map<String, Long> tokens = this.tokenStore.listUserTokens(userId);
        if (tokens.isEmpty()) {
            return 0;
        }
        this.jtiBlacklist.blacklist(tokens);
        this.tokenStore.removeUserTokens(userId);
        for (String token : tokens.keySet()) {
            // 刷新令牌存在redis里，索引清空后仍需逐个删除，否则还能用来换新令牌
            this.tokenStore.removeRefreshToken(token);
        }
        log.info("已作废用户的全部令牌，userId={}，数量={}", userId, tokens.size());
        return tokens.size();
    }

    /**
     * 取用户id，非{@link LoginUser}时返回null
     *
     * @param userDetails 登录用户
     * @return 用户id
     */
    private Long resolveUserId(UserDetails userDetails) {
        return userDetails instanceof LoginUser loginUser ? loginUser.getId() : null;
    }

    /**
     * 取角色编码列表，只取角色类型的授权
     *
     * @param userDetails 登录用户
     * @return 角色编码列表
     */
    private List<String> resolveRoleCodes(UserDetails userDetails) {
        if (userDetails == null || userDetails.getAuthorities() == null) {
            return List.of();
        }
        return userDetails.getAuthorities().stream()
                .filter(a -> a instanceof RolePermissionGrantedAuthority rpa
                        && rpa.getType() == RolePermissionGrantedAuthority.AuthorityType.ROLE)
                .map(a -> ((RolePermissionGrantedAuthority) a).getAuthority())
                .toList();
    }

}
