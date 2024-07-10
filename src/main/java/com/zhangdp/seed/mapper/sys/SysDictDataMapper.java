package com.zhangdp.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhangdp.seed.entity.sys.SysDictData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 2023/4/12 字典数据mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 根据字典id查询列表并按sorts字段升序
     *
     * @param dictId
     * @return
     */
    default List<SysDictData> selectListByDictIdOrderBySorts(Long dictId) {
        return this.selectList(Wrappers.lambdaQuery(SysDictData.class)
                .eq(SysDictData::getDictId, dictId)
                .orderByAsc(SysDictData::getSorts));
    }
}
