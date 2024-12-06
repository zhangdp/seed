package io.github.seed.common.security;

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
     * 是否自动刷新访问令牌过期时间
     */
    boolean IS_AUTO_RENEW = true;
    /**
     * 是否启动刷新token
     */
    boolean ENABLE_REFRESH_TOKEN = true;

    /**
     * 访问令牌过期时间配置key
     */
    String ACCESS_TOKEN_TTL_CONFIG_KEY = "ACCESS_TOKEN_TTL";
    /**
     * 刷新令牌过期时间配置key
     */
    String REFRESH_TOKEN_TTL_CONFIG_KEY = "REFRESH_TOKEN_TTL";
    /**
     * 是否自动刷新令牌过期时间配置key
     */
    String IS_AUTO_RENEW_CONFIG_KEY = "IS_AUTO_RENEW";
    /**
     * 是否启动刷新令牌配置key
     */
    String ENABLE_REFRESH_TOKEN_CONFIG_KEY = "ENABLE_REFRESH_TOKEN";

    /**
     * redis 访问令牌key前缀
     */
    String REDIS_ACCESS_TOKEN_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "access_token";
    /**
     * redis 用户-访问令牌key前缀
     */
    String REDIS_USER_TO_ACCESS_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "user_to_access";
    /**
     * redis 刷新令牌key前缀
     */
    String REDIS_REFRESH_TOKEN_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "refresh_token";
    /**
     * redis 访问令牌-刷新令牌key前缀
     */
    String REDIS_ACCESS_TO_REFRESH_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "access_to_refresh";

}
