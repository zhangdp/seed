package io.github.seed.common.security.service;

import io.github.seed.common.security.SecurityConst;
import io.github.seed.common.security.data.AccessToken;
import io.github.seed.common.security.data.RefreshToken;
import io.github.seed.service.sys.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.UUID;

/**
 * 2024/6/27 token服务
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final TokenStore tokenStore;
    private final ConfigService configService;

    /**
     * 创建token
     *
     * @return
     */
    public AccessToken createToken(UserDetails userDetails) {
        int ttl = this.getAccessTokenTtlConfig();
        String token = this.generateToken();
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setExpiresIn(ttl);
        accessToken.setUserDetails(userDetails);
        accessToken.setIssuedAt(System.currentTimeMillis());
        if (this.getEnableRefreshToken()) {
            RefreshToken refreshToken = this.createRefreshToken(token, userDetails);
            accessToken.setRefreshToken(refreshToken);
        }
        Duration accessTokenDuration = Duration.ofSeconds(ttl);
        tokenStore.storeAccessToken(accessToken, accessTokenDuration);
        tokenStore.storeUserToAccessToken(userDetails.getUsername(), accessToken.getToken(), accessTokenDuration);
        if (accessToken.getRefreshToken() != null) {
            tokenStore.storeRefreshToken(accessToken.getRefreshToken(), Duration.ofSeconds(accessToken.getRefreshToken().getExpiresIn()));
        }
        return accessToken;
    }

    /**
     * 生成refreshToken
     *
     * @param accessToken
     * @param userDetails
     * @return
     */
    private RefreshToken createRefreshToken(String accessToken, UserDetails userDetails) {
        RefreshToken refreshToken = new RefreshToken();
        String token = this.generateToken();
        refreshToken.setToken(token);
        refreshToken.setAccessToken(accessToken);
        refreshToken.setIssuedAt(System.currentTimeMillis());
        refreshToken.setExpiresIn(this.getRefreshTokenTtlConfig());
        refreshToken.setUsername(userDetails.getUsername());
        return refreshToken;
    }

    /**
     * 根据accessToken获取登录用户
     *
     * @param accessToken
     * @return
     */
    public UserDetails loadUserDetails(String accessToken) {
        AccessToken access = this.loadAccessToken(accessToken);
        if (access == null) {
            return null;
        }
        return access.getUserDetails();
    }

    /**
     * 获取访问令牌信息
     *
     * @param accessToken
     * @return
     */
    public AccessToken loadAccessToken(String accessToken) {
        return tokenStore.loadAccessToken(accessToken);
    }

    /**
     * 生成token字符串
     *
     * @return
     */
    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * 删除token
     *
     * @param accessToken
     * @return
     */
    public boolean removeToken(String accessToken) {
        AccessToken access = tokenStore.loadAccessToken(accessToken);
        if (access == null) {
            return false;
        }
        UserDetails userDetails = access.getUserDetails();
        tokenStore.removeAccessToken(accessToken);
        tokenStore.removeUserToAccessToken(userDetails.getUsername(), accessToken);
        RefreshToken refresh = access.getRefreshToken();
        if (refresh != null) {
            tokenStore.removeRefreshToken(refresh.getToken());
        }
        return true;
    }

    /**
     * 加载刷新令牌
     *
     * @param refreshToken
     * @return
     */
    public RefreshToken loadRefreshToken(String refreshToken) {
        return tokenStore.loadRefreshToken(refreshToken);
    }

    /**
     * 重置令牌过期时间
     *
     * @param accessToken
     */
    public boolean resetTokenExpire(String accessToken) {
        AccessToken access = tokenStore.loadAccessToken(accessToken);
        if (access == null) {
            return false;
        }
        return this.resetTokenExpire(access);
    }

    /**
     * 重置令牌过期时间
     *
     * @param accessToken
     * @return
     */
    public boolean resetTokenExpire(AccessToken accessToken) {
        return this.resetTokenExpire(accessToken.getToken(), accessToken.getUserDetails());
    }

    /**
     * 重置令牌过期时间
     *
     * @param accessToken
     * @param userDetails
     * @return
     */
    public boolean resetTokenExpire(String accessToken, UserDetails userDetails) {
        Duration expire = Duration.ofSeconds(this.getAccessTokenTtlConfig());
        boolean ret = tokenStore.updateAccessTokenExpire(accessToken, expire);
        tokenStore.updateUserToAccessTokenExpire(userDetails.getUsername(), accessToken, expire);
        return ret;
    }

    /**
     * 如果有必要则重置令牌过期时间
     *
     * @param accessToken
     * @param userDetails
     */
    public void resetTokenExpireIfNecessary(String accessToken, UserDetails userDetails) {
        if (this.isAutoRenewConfig()) {
            this.resetTokenExpire(accessToken, userDetails);
        }
    }

    /**
     * 如果有必要则重置令牌过期时间
     *
     * @param accessToken
     */
    public void resetTokenExpireIfNecessary(String accessToken) {
        if (this.isAutoRenewConfig()) {
            this.resetTokenExpire(accessToken);
        }
    }

    /**
     * 如果有必要则重置令牌过期时间
     *
     * @param accessToken
     */
    public void resetTokenExpireIfNecessary(AccessToken accessToken) {
        if (this.isAutoRenewConfig()) {
            this.resetTokenExpire(accessToken);
        }
    }

    /**
     * 获取是否自动刷新访问令牌过期时间配置
     *
     * @return
     */
    private boolean isAutoRenewConfig() {
        return configService.getConfigBoolValue(SecurityConst.IS_AUTO_RENEW_CONFIG_KEY, SecurityConst.IS_AUTO_RENEW);
    }

    /**
     * 获取是否启用刷新token
     *
     * @return
     */
    private boolean getEnableRefreshToken() {
        return configService.getConfigBoolValue(SecurityConst.ENABLE_REFRESH_TOKEN_CONFIG_KEY, SecurityConst.ENABLE_REFRESH_TOKEN);
    }

    /**
     * 获取访问令牌过期时间配置
     *
     * @return
     */
    private int getAccessTokenTtlConfig() {
        return configService.getConfigIntValue(SecurityConst.ACCESS_TOKEN_TTL_CONFIG_KEY, SecurityConst.ACCESS_TOKEN_TTL);
    }

    /**
     * 获取刷新令牌过期时间配置
     *
     * @return
     */
    private int getRefreshTokenTtlConfig() {
        return configService.getConfigIntValue(SecurityConst.REFRESH_TOKEN_TTL_CONFIG_KEY, SecurityConst.REFRESH_TOKEN_TTL);
    }

}
