package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Resource;
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


}
