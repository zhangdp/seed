package io.github.seed.service.sys;

import io.github.seed.entity.sys.Role;

import java.util.List;

/**
 * 2023/4/3 角色service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface RoleService {

    /**
     * 获取某个用户的角色列表
     *
     * @param userId
     * @return
     */
    List<Role> listUserRoles(Long userId);

    /**
     * 根据角色标识获取
     *
     * @param code
     * @return
     */
    Role getByCode(String code);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(Role entity);
}
