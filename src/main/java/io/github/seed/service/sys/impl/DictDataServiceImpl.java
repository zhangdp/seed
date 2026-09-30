package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.constant.CacheConst;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.DictData;
import io.github.seed.mapper.sys.DictDataMapper;
import io.github.seed.service.sys.DictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @CacheEvict(key = "'" + CACHE_LIST + "' + #entity.dictId")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(DictData entity) {
        if (entity.getSorts() == null) {
            entity.setSorts(0);
        }
        return dictDataMapper.insert(entity) > 0;
    }

    @CacheEvict(key = "'" + CACHE_LIST + "' + #entity.dictId")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(DictData entity) {
        DictData bean = this.dictDataMapper.selectOneById(entity.getId());
        Assert.notNull(bean, () -> new BizException(ErrorCode.NOT_FOUND.code(), "字典项不存在"));
        DictData update = new DictData();
        update.setId(entity.getId());
        update.setDictId(bean.getDictId());
        update.setValue(entity.getValue());
        update.setLabel(entity.getLabel());
        update.setDescription(entity.getDescription());
        update.setMetaData(entity.getMetaData());
        update.setSorts(entity.getSorts());
        // 所属字典不允许修改
        return dictDataMapper.update(update) > 0;
    }

    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(Long id) {
        DictData bean = this.dictDataMapper.selectOneById(id);
        if (bean == null) {
            return false;
        }
        return dictDataMapper.deleteById(id) > 0;
    }
}
