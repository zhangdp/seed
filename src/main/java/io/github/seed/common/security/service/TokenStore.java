package io.github.seed.common.security.service;

import io.github.seed.common.security.data.AccessToken;
import io.github.seed.common.security.data.RefreshToken;

import java.time.Duration;

/**
 * 2023/9/1 token存储接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface TokenStore {

    /**
     * 保存访问令牌
     *
     * @param accessToken
     * @param expire
     */
    void storeAccessToken(AccessToken accessToken, Duration expire);

    /**
     * 获取访问令牌
     *
     * @param accessToken
     * @return
     */
    AccessToken loadAccessToken(String accessToken);

    /**
     * 删除访问令牌
     *
     * @param accessToken
     * @return
     */
    boolean removeAccessToken(String accessToken);

    /**
     * 更新访问令牌过期时间
     *
     * @param accessToken
     * @param expire
     * @return
     */
    boolean updateAccessTokenExpire(String accessToken, Duration expire);

    /**
     * 保存刷新令牌
     *
     * @param refreshToken
     * @param expire
     */
    void storeRefreshToken(RefreshToken refreshToken, Duration expire);

    /**
     * 获取刷新令牌
     *
     * @param refreshToken
     * @return
     */
    RefreshToken loadRefreshToken(String refreshToken);

    /**
     * 删除刷新令牌
     *
     * @param refreshToken
     * @return
     */
    boolean removeRefreshToken(String refreshToken);

    /**
     * 更新刷新令牌过期时间
     *
     * @param refreshToken
     * @param expire
     * @return
     */
    boolean updateRefreshTokenExpire(String refreshToken, Duration expire);

    /**
     * 保存用户的访问令牌
     *
     * @param username
     * @param accessToken
     * @param expire
     */
    void storeUserToAccessToken(String username, String accessToken, Duration expire);

    /**
     * 修改用户的访问令牌过期时间
     *
     * @param username
     * @param accessToken
     * @param expire
     */
    void updateUserToAccessTokenExpire(String username, String accessToken, Duration expire);

    /**
     * 获取用户访问令牌数
     *
     * @param username
     * @return
     */
    int countUserToAccessToken(String username);

    /**
     * 清理用户已过期token
     *
     * @param username
     * @param expire
     */
    void pruneExpiredUserToAccessToken(String username, Duration expire);

    /**
     * 删除用户某个访问令牌
     *
     * @param username
     * @param accessToken
     */
    void removeUserToAccessToken(String username, String accessToken);

}
