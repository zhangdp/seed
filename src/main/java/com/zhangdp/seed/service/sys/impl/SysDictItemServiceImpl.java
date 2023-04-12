package com.zhangdp.seed.service.sys.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.common.constant.CacheConst;
import com.zhangdp.seed.entity.sys.SysDictItem;
import com.zhangdp.seed.mapper.sys.SysDictItemMapper;
import com.zhangdp.seed.service.sys.SysDictItemService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 2023/4/12 字典项service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@CacheConfig(cacheNames = CacheConst.CACHE_SYS_DICT_ITEM)
public class SysDictItemServiceImpl extends ServiceImpl<SysDictItemMapper, SysDictItem> implements SysDictItemService {

    private static final String CACHE_LIST = "list" + CacheConst.SPLIT;

    @Cacheable(key = "'" + CACHE_LIST + "' + #dictId", condition = "#result != null && #result.size() > 0")
    @Override
    public List<SysDictItem> listByDictId(Long dictId) {
        return this.list(Wrappers.lambdaQuery(SysDictItem.class)
                .eq(SysDictItem::getDictId, dictId)
                .orderByAsc(SysDictItem::getSorts));
    }
}
