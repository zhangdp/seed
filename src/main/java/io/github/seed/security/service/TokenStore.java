package io.github.seed.security.service;

import org.springframework.security.core.userdetails.UserDetails;

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
     * @param token
     * @param userDetails
     * @param expiresIn
     */
    void storeAccessToken(String token, UserDetails userDetails, long expiresIn);

    /**
     * 获取访问令牌
     *
     * @param token
     * @return
     */
    UserDetails loadAccessToken(String token);

    /**
     * 删除访问令牌
     *
     * @param token
     * @return
     */
    boolean removeAccessToken(String token);

    /**
     * 是否存在访问令牌
     *
     * @param token
     * @return
     */
    boolean existsAccessToken(String token);

    /**
     * 获取访问令牌
     *
     * @param token
     * @return
     */
    long getAccessTokenExpiresIn(String token);

    /**
     * 更新访问令牌过期时间
     *
     * @param token
     * @param expiresIn
     * @return
     */
    boolean updateAccessTokenExpiresIn(String token, long expiresIn);
}
