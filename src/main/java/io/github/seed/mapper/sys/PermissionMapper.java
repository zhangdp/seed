package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Permission;
import io.github.seed.entity.sys.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 权限mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据id判断是否存在
     *
     * @param id
     * @return
     */
    default boolean existsById(Long id) {
        return this.selectCountByQuery(QueryWrapper.create().eq(Permission::getId, id)) > 0;
    }

    /**
     * 根据批量id来in查询
     *
     * @param ids
     * @return
     */
    default List<Permission> selectListByIdIn(Collection<Long> ids) {
        return this.selectListByIds(ids);
    }

    /**
     * 根据角色id列表批量in查询
     *
     * @param roleIds
     * @return
     */
    default List<Permission> selectListByRoleIdIn(Collection<Long> roleIds) {
        QueryWrapper subQuery = QueryWrapper.create().select(RolePermission::getPermissionId).from(RolePermission.class).in(RolePermission::getRoleId, roleIds);
        return this.selectListByQuery(QueryWrapper.create().in(Permission::getId, subQuery));
    }

    /**
     * 根据角色id查询
     *
     * @param roleId
     * @return
     */
    default List<Permission> selectListByRoleId(Long roleId) {
        QueryWrapper subQuery = QueryWrapper.create().select(RolePermission::getPermissionId).from(RolePermission.class).eq(RolePermission::getRoleId, roleId);
        return this.selectListByQuery(QueryWrapper.create().in(Permission::getId, subQuery));
    }
}
