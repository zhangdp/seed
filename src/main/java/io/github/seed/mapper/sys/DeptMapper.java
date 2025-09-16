package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Dept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/3 部门mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 根据id判断是否存在
     *
     * @param id
     * @return
     */
    default boolean existsById(Long id) {
        return this.selectCountByQuery(QueryWrapper.create().eq(Dept::getId, id)) > 0;
    }
}
