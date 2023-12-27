package com.zhangdp.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangdp.seed.entity.sys.SysDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 2023/4/3 部门mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 查询全部
     *
     * @return
     */
    default List<SysDept> selectAll() {
        return this.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 根据id判断是否存在
     *
     * @param id
     * @return
     */
    default boolean existsById(Long id) {
        return this.exists(Wrappers.lambdaQuery(SysDept.class).eq(SysDept::getId, id));
    }
}
