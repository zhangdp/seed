package io.github.seed.module.security.data;

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
     * redis jti黑名单key，zset：member=jti，score=拉黑到期时间戳（毫秒）
     */
    String REDIS_JTI_BLACKLIST = REDIS_PREFIX + REDIS_SPLIT + "jti_blacklist";
    /**
     * redis jti黑名单版本号key，每次变更自增，节点据此判断内存是否过期
     */
    String REDIS_JTI_BLACKLIST_VERSION = REDIS_JTI_BLACKLIST + REDIS_SPLIT + "version";
    /**
     * redis jti黑名单变更频道，只广播版本号不广播明细
     */
    String REDIS_JTI_BLACKLIST_CHANNEL = REDIS_JTI_BLACKLIST + REDIS_SPLIT + "changed";
    /**
     * redis 用户-凭证索引key前缀，zset：member=凭证标识（访问令牌的jti或刷新令牌本身），score=到期时间戳（毫秒）
     * <br>用于改密码、禁用账号、管理员踢人时把该用户已签发的令牌一次性拉黑
     */
    String REDIS_USER_TOKEN_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "user_token";
    /**
     * redis 角色权限版本号key，角色权限变更后自增
     */
    String REDIS_ROLE_PERMISSION_VERSION = REDIS_PREFIX + REDIS_SPLIT + "role_permission" + REDIS_SPLIT + "version";
    /**
     * redis 角色权限变更频道，只广播版本号
     */
    String REDIS_ROLE_PERMISSION_CHANNEL = REDIS_PREFIX + REDIS_SPLIT + "role_permission" + REDIS_SPLIT + "changed";

    /**
     * request attr 访问令牌
     */
    String REQUEST_ATTR_ACCESS_TOKEN = "security_access_token";
    /**
     * request attr 登录时提交的原始认证对象，由{@code SecurityService}在认证前写入：
     * 认证异常不携带authentication，失败时只能从这里取到提交的用户名
     */
    String REQUEST_ATTR_LOGIN_AUTHENTICATION = "security_login_authentication";

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
     * 刷新令牌头，登出时随访问令牌一起提交，用于同时作废刷新令牌
     */
    String REFRESH_TOKEN_HEADER = "Refresh-Token";

    /**
     * token参数名称
     */
    String AUTHORIZATION_PARAMETER = "access_token";
}
