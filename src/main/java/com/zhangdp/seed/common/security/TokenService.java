package com.zhangdp.seed.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.data.id.UUID;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 2024/6/27 token服务
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenService {

    private final TokenStore tokenStore;

    /**
     * 创建token
     *
     * @return
     */
    public TokenInfo createToken(UserDetails userDetails) {
        String accessToken = this.generateToken();
        tokenStore.storeAccessToken(accessToken, userDetails, SecurityConst.ACCESS_TOKEN_TTL);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setAccessToken(accessToken);
        tokenInfo.setAccessTokenExpiresIn(SecurityConst.ACCESS_TOKEN_TTL);
        return tokenInfo;
    }

    /**
     * 根据accessToken获取登录信息
     *
     * @param accessToken
     * @return
     */
    public UserDetails loadPrincipal(String accessToken) {
        return tokenStore.loadAccessToken(accessToken);
    }

    /**
     * 生成token字符串
     *
     * @return
     */
    private String generateToken() {
        return UUID.randomUUID().toString();
    }

}
