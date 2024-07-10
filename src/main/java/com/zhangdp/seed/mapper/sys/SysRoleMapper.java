package com.zhangdp.seed.mapper.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangdp.seed.entity.sys.SysRole;
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
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据批量角色id来in查询列表
     *
     * @param roleIds
     * @return
     */
    default List<SysRole> selectListByRoleIdIn(Collection<Long> roleIds) {
        return this.selectList(lambdaQuery()
                .in(SysRole::getId, roleIds));
    }

    /**
     * 根据code查询单条记录
     *
     * @param code
     * @return
     */
    default SysRole selectOneByCode(String code) {
        return this.selectOne(lambdaQuery().eq(SysRole::getCode, code));
    }

    /**
     * 获取MP LambdaQueryWrapper
     *
     * @return
     */
    default LambdaQueryWrapper<SysRole> lambdaQuery() {
        return Wrappers.lambdaQuery(SysRole.class);
    }
}
