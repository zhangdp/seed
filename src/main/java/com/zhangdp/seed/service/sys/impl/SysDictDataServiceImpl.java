package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.common.constant.CacheConst;
import com.zhangdp.seed.common.constant.TableNameConst;
import com.zhangdp.seed.entity.sys.SysDictData;
import com.zhangdp.seed.mapper.sys.SysDictDataMapper;
import com.zhangdp.seed.service.sys.SysDictDataService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2023/4/12 字典数据service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@CacheConfig(cacheNames = TableNameConst.SYS_DICT_DATA)
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    private static final String CACHE_LIST = "list" + CacheConst.SPLIT;

    @Cacheable(key = "'" + CACHE_LIST + "' + #dictId", condition = "#result != null && #result.size() > 0")
    @Override
    public List<SysDictData> listByDictId(Long dictId) {
        return this.list(Wrappers.lambdaQuery(SysDictData.class)
                .eq(SysDictData::getDictId, dictId)
                .orderByAsc(SysDictData::getSorts));
    }
}
