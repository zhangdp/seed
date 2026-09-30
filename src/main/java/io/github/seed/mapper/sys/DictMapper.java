package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.logicdelete.LogicDeleteManager;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Dict;
import io.github.seed.model.PageData;
import io.github.seed.model.query.BaseTextQuery;
import io.github.seed.model.query.PageQuery;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/12 字典mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 根据类型查询单条记录
     *
     * @param type
     * @return
     */
    default Dict selectOneByType(String type) {
        return this.selectOneByQuery(QueryWrapper.create().eq(Dict::getType, type));
    }

    /**
     * 统计指定类型但id不为指定id的字典个数（包含已逻辑删除的）
     *
     * @param type
     * @param id
     * @return
     */
    default boolean existsByTypeAndIdNot(String type, Long id) {
        return LogicDeleteManager.execWithoutLogicDelete(() -> this.selectCountByQuery(QueryWrapper.create()
                .eq(Dict::getType, type)
                .ne(Dict::getId, id, id != null)) > 0);
    }

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    default PageData<Dict> queryPage(PageQuery<BaseTextQuery> pageQuery) {
        BaseTextQuery params = pageQuery.getParams();
        QueryWrapper wrapper = QueryWrapper.create().orderBy(pageQuery.getOrderBy());
        if (params != null) {
            String query = params.getQuery();
            wrapper.and(w -> {
                w.like(Dict::getName, query);
                w.or(Dict::getType).like(query);
            });
        }
        Page<Dict> page = this.paginate(pageQuery.getPage(), pageQuery.getSize(), pageQuery.getTotal(), wrapper);
        return new PageData<>(page.getRecords(), page.getTotalRow(), page.getPageNumber(), page.getPageSize());
    }
}
