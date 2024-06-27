package com.zhangdp.seed.common.security;

/**
 * 2023/4/4 认证相关常量
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SecurityConst {

    /**
     * session key 用户信息
     */
    String SESSION_USER = "user";
    /**
     * 认证类型:BEARER
     */
    String AUTH_TYPE_BEARER = "Bearer";
    /**
     * 认证头
     */
    String AUTHORIZATION_HEADER = "Authorization";
    /**
     * 认证参数名称
     */
    String AUTHORIZATION_PARAMETER = "access_token";
    /**
     * 角色前缀
     */
    String ROLE_PREFIX = "ROLE_";
}
