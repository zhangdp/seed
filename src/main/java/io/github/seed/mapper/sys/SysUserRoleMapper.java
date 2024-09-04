package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.seed.entity.sys.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 2023/4/3 用户角色mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户id查询列表
     *
     * @param userId
     * @return
     */
    default List<SysUserRole> selectListByUserId(Long userId) {
        return this.selectList(Wrappers.lambdaQuery(SysUserRole.class).eq(SysUserRole::getUserId, userId));
    }
}
