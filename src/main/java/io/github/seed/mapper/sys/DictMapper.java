package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.entity.sys.Dict;
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
}
