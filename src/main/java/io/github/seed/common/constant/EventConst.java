package io.github.seed.common.constant;

/**
 * 2024/10/4 事件名称常量
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface EventConst {

    /**
     * 新增用户
     */
    String ADD_USER = "ADD_USER";
    /**
     * 修改用户
     */
    String UPDATE_USER = "UPDATE_USER";
    /**
     * 角色权限关系变化：权限增删改、新增角色，各节点据此刷新角色权限缓存
     */
    String ROLE_PERMISSION_CHANGE = "ROLE_PERMISSION_CHANGE";
    /**
     * 用户角色关系变化，被改的用户已签发令牌里的角色是旧快照，需要踢下线重新登录
     */
    String USER_ROLE_CHANGE = "USER_ROLE_CHANGE";
    String TEST = "TEST";
}
