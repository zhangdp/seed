package io.github.seed.service.sys.impl;

import io.github.seed.common.constant.CacheConst;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.sys.DictData;
import io.github.seed.mapper.sys.DictDataMapper;
import io.github.seed.service.sys.DictDataService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class DictDataServiceImpl implements DictDataService {

    private static final String CACHE_LIST = "list" + CacheConst.SPLIT;

    private final DictDataMapper dictDataMapper;

    @Cacheable(key = "'" + CACHE_LIST + "' + #dictId", condition = "#result != null && #result.size() > 0")
    @Override
    public List<DictData> listByDictId(Long dictId) {
        return dictDataMapper.selectListByDictIdOrderBySorts(dictId);
    }
}
