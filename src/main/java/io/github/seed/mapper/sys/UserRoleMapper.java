package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.seed.entity.sys.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 2023/4/3 用户角色mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    /**
     * 根据用户id查询列表
     *
     * @param userId
     * @return
     */
    default List<UserRole> selectListByUserId(Long userId) {
        return this.selectList(Wrappers.lambdaQuery(UserRole.class).eq(UserRole::getUserId, userId));
    }

    /**
     * 是否存在
     *
     * @param userId
     * @param roleId
     * @return
     */
    default boolean existsByUserIdAndRoleId(Long userId, Long roleId) {
        return this.exists(Wrappers.lambdaQuery(UserRole.class).eq(UserRole::getUserId, userId).eq(UserRole::getRoleId, roleId));
    }
}
