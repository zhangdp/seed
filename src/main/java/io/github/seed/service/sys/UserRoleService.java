package io.github.seed.service.sys;

import io.github.seed.entity.sys.UserRole;

import java.util.Collection;
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

    /**
     * 如果不存在则新增
     *
     * @param userId
     * @param roleId
     * @return
     */
    boolean addIfAbsent(Long userId, Long roleId);

    /**
     * 是否存在
     *
     * @param userId
     * @param roleId
     * @return
     */
    boolean exists(Long userId, Long roleId);

    /**
     * 批量添加
     *
     * @param entities
     * @return
     */
    boolean addBatch(Collection<UserRole> entities);
}
