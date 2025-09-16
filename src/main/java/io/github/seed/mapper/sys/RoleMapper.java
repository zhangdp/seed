package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 2023/4/3 角色mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据批量角色id来in查询列表
     *
     * @param roleIds
     * @return
     */
    default List<Role> selectListByRoleIdIn(Collection<Long> roleIds) {
        return this.selectListByQuery(QueryWrapper.create().in(Role::getId, roleIds));
    }

    /**
     * 根据code查询单条记录
     *
     * @param code
     * @return
     */
    default Role selectOneByCode(String code) {
        return this.selectOneByQuery(QueryWrapper.create().eq(Role::getCode, code));
    }

    /**
     * 根据角色id查询是否存在
     *
     * @param roleId
     * @return
     */
    default boolean exists(Long roleId) {
        return this.selectCountByQuery(QueryWrapper.create().eq(Role::getId, roleId)) > 0;
    }

}
