package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.seed.entity.sys.RoleResource;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 * 2023/4/12 角色关联资源mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface RoleResourceMapper extends BaseMapper<RoleResource> {

    /**
     * 根据角色id查询列表
     *
     * @param roleId
     * @return
     */
    default List<RoleResource> selectListByRoleId(Long roleId) {
        return this.selectList(lambdaQuery().eq(RoleResource::getRoleId, roleId));
    }

    /**
     * 根据批量角色id来in查询列表
     *
     * @param roleIds
     * @return
     */
    default List<RoleResource> selectListByRoleIdIn(Collection<Long> roleIds) {
        return this.selectList(lambdaQuery().in(RoleResource::getRoleId, roleIds));
    }

    /**
     * 获取MP LambdaQueryWrapper
     *
     * @return
     */
    default LambdaQueryWrapper<RoleResource> lambdaQuery() {
        return Wrappers.lambdaQuery(RoleResource.class);
    }
}
