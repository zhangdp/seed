package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Resource;
import io.github.seed.entity.sys.RoleResource;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 2023/4/12 资源mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface ResourceMapper extends BaseMapper<Resource> {

    /**
     * 根据id判断是否存在
     *
     * @param id
     * @return
     */
    default boolean existsById(Long id) {
        return this.selectCountByQuery(QueryWrapper.create().eq(Resource::getId, id)) > 0;
    }

    /**
     * 根据批量id来in查询
     *
     * @param ids
     * @return
     */
    default List<Resource> selectListByIdIn(Collection<Long> ids) {
        return this.selectListByIds(ids);
    }

    /**
     * 根据角色id列表批量in查询
     *
     * @param roleIds
     * @return
     */
    default List<Resource> selectListByRoleIdIn(Collection<Long> roleIds) {
        QueryWrapper subQuery = QueryWrapper.create().select(RoleResource::getResourceId).from(RoleResource.class).in(RoleResource::getRoleId, roleIds);
        return this.selectListByQuery(QueryWrapper.create().in(Resource::getId, subQuery));
    }

    /**
     * 根据角色id查询
     *
     * @param roleId
     * @return
     */
    default List<Resource> selectListByRoleId(Long roleId) {
        QueryWrapper subQuery = QueryWrapper.create().select(RoleResource::getResourceId).from(RoleResource.class).eq(RoleResource::getRoleId, roleId);
        return this.selectListByQuery(QueryWrapper.create().in(Resource::getId, subQuery));
    }
}
