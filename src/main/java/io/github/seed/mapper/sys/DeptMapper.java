package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.seed.entity.sys.Dept;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 2023/4/3 部门mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 查询全部
     *
     * @return
     */
    default List<Dept> selectAll() {
        return this.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 根据id判断是否存在
     *
     * @param id
     * @return
     */
    default boolean existsById(Long id) {
        return this.exists(Wrappers.lambdaQuery(Dept.class).eq(Dept::getId, id));
    }
}
