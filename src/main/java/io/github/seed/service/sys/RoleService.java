package io.github.seed.service.sys;

import io.github.seed.entity.sys.Role;
import io.github.seed.model.PageData;
import io.github.seed.model.query.BaseTextQuery;
import io.github.seed.model.query.PageQuery;

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
     * 获取全部角色，供角色权限缓存预热
     *
     * @return 全部角色
     */
    List<Role> listAll();

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

    /**
     * 是否存在
     *
     * @param roleId
     * @return
     */
    boolean exists(Long roleId);

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    PageData<Role> queryPage(PageQuery<BaseTextQuery> pageQuery);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean update(Role entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 查询角色已分配的权限id列表
     *
     * @param roleId
     * @return
     */
    List<Long> listPermissionIds(Long roleId);

    /**
     * 保存角色权限
     *
     * @param roleId
     * @param permissionIds
     * @return
     */
    boolean savePermissions(Long roleId, List<Long> permissionIds);
}
