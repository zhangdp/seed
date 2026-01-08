package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.RolePermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 角色关联权限mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

    /**
     * 根据角色id查询列表
     *
     * @param roleId
     * @return
     */
    default List<RolePermission> selectListByRoleId(Long roleId) {
        return this.selectListByQuery(QueryWrapper.create().eq(RolePermission::getRoleId, roleId));
    }

    /**
     * 根据批量角色id来in查询列表
     *
     * @param roleIds
     * @return
     */
    default List<RolePermission> selectListByRoleIdIn(Collection<Long> roleIds) {
        return this.selectListByQuery(QueryWrapper.create().in(RolePermission::getRoleId, roleIds));
    }

}
