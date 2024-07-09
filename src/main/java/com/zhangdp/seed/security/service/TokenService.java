package com.zhangdp.seed.security.service;

import com.zhangdp.seed.entity.sys.SysProperties;
import com.zhangdp.seed.security.SecurityConst;
import com.zhangdp.seed.security.data.TokenInfo;
import com.zhangdp.seed.service.sys.SysPropertiesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.data.id.UUID;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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
    private final SysPropertiesService sysPropertiesService;

    /**
     * 创建token
     *
     * @return
     */
    public TokenInfo createToken(UserDetails userDetails) {
        int ttl = this.getTokenTtlSetting();
        String accessToken = this.generateToken();
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setAccessToken(accessToken);
        tokenInfo.setAccessTokenExpiresIn(ttl);
        tokenStore.storeAccessToken(accessToken, userDetails, ttl);
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
        return tokenStore.removeAccessToken(accessToken);
    }

    /**
     * 重置访问令牌过期时间
     *
     * @param accessToken
     */
    public boolean resetTokenExpireIn(String accessToken) {
        return tokenStore.updateAccessTokenExpiresIn(accessToken, this.getTokenTtlSetting());
    }

    /**
     * 获取访问令牌过期时间配置
     *
     * @return
     */
    private int getTokenTtlSetting() {
        SysProperties properties = sysPropertiesService.getByCode(SecurityConst.ACCESS_TOKEN_TTL_PARAM_KEY);
        Assert.notNull(properties, "不存在配置" + SecurityConst.ACCESS_TOKEN_TTL_PARAM_KEY);
        return Integer.parseInt(properties.getTextValue());
    }
}
