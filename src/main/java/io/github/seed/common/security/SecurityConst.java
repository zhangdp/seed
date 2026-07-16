package io.github.seed.common.security;

/**
 * 2023/4/4 认证相关常量
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SecurityConst {

    /**
     * application配置前缀名称
     */
    String CONFIG_PREFIX = "app.security";

    /**
     * session key 用户信息
     */
    String SESSION_USER = "user";

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
     * 刷新token url
     */
    String REFRESH_TOKEN_URL = AUTH_URL + "/token/refresh";

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

    /**
     * request attr 访问令牌
     */
    String REQUEST_ATTR_ACCESS_TOKEN = "security_access_token";

    /**
     * ACTUATOR端点需要的角色
     */
    String ACTUATOR_NEED_ROLE = "ACTUATOR";

    /**
     * 认证类型：Bearer
     */
    String AUTH_TYPE_BEARER = "Bearer";

    /**
     * 认证类型：Basic
     */
    String AUTH_TYPE_BASIC = "Basic";

    /**
     * 认证头
     */
    String AUTHORIZATION_HEADER = "Authorization";

    /**
     * token参数名称
     */
    String AUTHORIZATION_PARAMETER = "access_token";
}
