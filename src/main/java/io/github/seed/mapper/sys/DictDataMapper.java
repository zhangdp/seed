package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.seed.entity.sys.DictData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 2023/4/12 字典数据mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface DictDataMapper extends BaseMapper<DictData> {

    /**
     * 根据字典id查询列表并按sorts字段升序
     *
     * @param dictId
     * @return
     */
    default List<DictData> selectListByDictIdOrderBySorts(Long dictId) {
        return this.selectList(Wrappers.lambdaQuery(DictData.class)
                .eq(DictData::getDictId, dictId)
                .orderByAsc(DictData::getSorts));
    }
}
