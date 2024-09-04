package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.seed.entity.sys.SysDict;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2023/4/12 字典mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SysDictMapper extends BaseMapper<SysDict> {

    /**
     * 根据类型查询单条记录
     *
     * @param type
     * @return
     */
    default SysDict selectOneByType(String type) {
        return this.selectOne(Wrappers.lambdaQuery(SysDict.class).eq(SysDict::getType, type));
    }
}
