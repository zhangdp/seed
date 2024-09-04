package io.github.seed.security;

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
    /**
     * 认证相关url
     */
    String AUTH_URL = "/auth";
    /**
     * 登录url
     */
    String LOGIN_URL = AUTH_URL + "/login";
    /**
     * 注销url
     */
    String LOGOUT_URL = AUTH_URL + "/logout";
    /**
     * 访问令牌有效期
     */
    int ACCESS_TOKEN_TTL = 30 * 60;
    /**
     * 刷新令牌有效期
     */
    int REFRESH_TOKEN_TTL = 24 * 60 * 60;
    /**
     * redis key 前缀
     */
    String REDIS_PREFIX = "auth";
    /**
     * redis key 分隔符
     */
    String REDIS_SPLIT = "::";
    /**
     * redis 访问令牌key前缀
     */
    String REDIS_ACCESS_TOKEN_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "access";
    /**
     * 访问令牌过期时间配置key
     */
    String ACCESS_TOKEN_TTL_PARAM_KEY = "ACCESS_TOKEN_TTL";
}
