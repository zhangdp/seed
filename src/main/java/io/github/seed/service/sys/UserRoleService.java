package io.github.seed.service.sys;

import io.github.seed.entity.sys.UserRole;

import java.util.List;

/**
 * 2023/4/3 用户角色service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface UserRoleService {

    /**
     * 根据用户id获取列表
     *
     * @param userId
     * @return
     */
    List<UserRole> listByUserId(Long userId);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(UserRole entity);
}
