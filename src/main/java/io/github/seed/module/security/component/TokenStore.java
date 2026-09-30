package io.github.seed.module.security.component;

import io.github.seed.module.security.data.RefreshToken;

import java.time.Duration;
import java.util.Map;

/**
 * 令牌存储
 * <br>只存无法自包含的东西：刷新令牌需要可吊销所以留在redis，访问令牌改jwt后已不再落库
 * <br>另有用户-凭证索引，用于按用户批量作废（改密码、禁用账号、踢人）
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface TokenStore {

    /**
     * 保存刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @param expire       有效期
     */
    void storeRefreshToken(RefreshToken refreshToken, Duration expire);

    /**
     * 获取刷新令牌
     *
     * @param refreshToken 刷新令牌字符串
     * @return 刷新令牌，不存在时返回null
     */
    RefreshToken loadRefreshToken(String refreshToken);

    /**
     * 删除刷新令牌
     *
     * @param refreshToken 刷新令牌字符串
     * @return 是否删除成功
     */
    boolean removeRefreshToken(String refreshToken);

    /**
     * 登记用户已签发的凭证，供按用户批量作废
     *
     * @param userId   用户id
     * @param tokenId  凭证标识：访问令牌为jti，刷新令牌为其自身
     * @param expireAt 凭证到期时间戳（毫秒）
     */
    void registerUserToken(Long userId, String tokenId, long expireAt);

    /**
     * 移除用户的某个凭证，登出时调用
     *
     * @param userId  用户id
     * @param tokenId 凭证标识
     */
    void removeUserToken(Long userId, String tokenId);

    /**
     * 取用户尚未过期的全部凭证，顺带清掉索引里的过期条目
     *
     * @param userId 用户id
     * @return 凭证标识与到期时间戳（毫秒），无有效凭证时为空
     */
    Map<String, Long> listUserTokens(Long userId);

    /**
     * 清空用户的凭证索引
     *
     * @param userId 用户id
     */
    void removeUserTokens(Long userId);

}
