package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
        return this.exists(this.lambdaQuery().eq(Resource::getId, id));
    }

    /**
     * 根据批量id来in查询
     *
     * @param ids
     * @return
     */
    default List<Resource> selectListByIdIn(Collection<Long> ids) {
        return this.selectList(this.lambdaQuery()
                .in(Resource::getId, ids)
                .orderByAsc(Resource::getParentId)
                .orderByAsc(Resource::getSorts));
    }

    /**
     * 查询全部
     *
     * @return
     */
    default List<Resource> selectAll() {
        return this.selectList(Wrappers.emptyWrapper());
    }

    /**
     * 返回MP LambdaQueryWrapper
     *
     * @return
     */
    default LambdaQueryWrapper<Resource> lambdaQuery() {
        return Wrappers.lambdaQuery(Resource.class);
    }

}
