package io.github.seed.common.security.service;

import cn.hutool.v7.core.data.id.IdUtil;
import io.github.seed.common.security.data.AccessToken;
import io.github.seed.common.security.data.RefreshToken;
import io.github.seed.common.security.SecurityProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;

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
    private final SecurityProperties securityProperties;

    // private FIFOCache<String, AccessToken> accessTokenCache = new FIFOCache<>(200);

    /**
     * 创建token
     *
     * @return
     */
    public AccessToken createToken(UserDetails userDetails) {
        Duration ttl = securityProperties.getAccessTokenTtl();
        String token = this.generateToken();
        AccessToken accessToken = new AccessToken();
        accessToken.setToken(token);
        accessToken.setExpiresIn(ttl.toSeconds());
        accessToken.setUserDetails(userDetails);
        accessToken.setIssuedAt(System.currentTimeMillis());
        if (securityProperties.isEnableRefreshToken()) {
            RefreshToken refreshToken = this.createRefreshToken(token, userDetails);
            accessToken.setRefreshToken(refreshToken);
        }
        tokenStore.storeAccessToken(accessToken, ttl);
        tokenStore.storeUserToAccessToken(userDetails.getUsername(), accessToken.getToken(), ttl);
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
        refreshToken.setExpiresIn(securityProperties.getRefreshTokenTtl().toSeconds());
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
        // if (accessTokenCache.containsKey(accessToken)) {
        //     return accessTokenCache.get(accessToken, false);
        // }
        AccessToken token = tokenStore.loadAccessToken(accessToken);
        // accessTokenCache.put(accessToken, token, 10000L);
        return token;
    }

    /**
     * 生成token字符串
     *
     * @return
     */
    public String generateToken() {
        return IdUtil.fastUUID();
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
        Duration ttl = securityProperties.getAccessTokenTtl();
        boolean ret = tokenStore.updateAccessTokenExpire(accessToken, ttl);
        tokenStore.updateUserToAccessTokenExpire(userDetails.getUsername(), accessToken, ttl);
        return ret;
    }

    /**
     * 如果有必要则重置令牌过期时间
     *
     * @param accessToken
     * @param userDetails
     */
    public void resetTokenExpireIfNecessary(String accessToken, UserDetails userDetails) {
        if (securityProperties.isAutoRenew()) {
            this.resetTokenExpire(accessToken, userDetails);
        }
    }

    /**
     * 如果有必要则重置令牌过期时间
     *
     * @param accessToken
     */
    public void resetTokenExpireIfNecessary(String accessToken) {
        if (securityProperties.isAutoRenew()) {
            this.resetTokenExpire(accessToken);
        }
    }

    /**
     * 如果有必要则重置令牌过期时间
     *
     * @param accessToken
     */
    public void resetTokenExpireIfNecessary(AccessToken accessToken) {
        if (securityProperties.isAutoRenew()) {
            this.resetTokenExpire(accessToken);
        }
    }


}
