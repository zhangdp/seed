package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.Config;
import io.github.seed.mapper.sys.ConfigMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.sys.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2023/4/12 系统配置service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = TableNameConst.SYS_CONFIG)
public class ConfigServiceImpl implements ConfigService {

    private final ConfigMapper configMapper;

    @Cacheable(key = "#key", unless = "#result == null")
    @Override
    public Config getByKey(String key) {
        return configMapper.selectOneByConfigKey(key);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(Config entity) {
        entity.setConfigKey(entity.getConfigKey().trim().toUpperCase());
        return configMapper.insert(entity) > 0;
    }

    @CacheEvict(key = "#entity.configKey", condition = "#result == true")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(Config entity) {
        Config bean = this.configMapper.selectById(entity.getId());
        Assert.notNull(bean, () -> new BizException(ErrorCode.PARAM_NOT_FOUND));
        Config update = new Config();
        update.setId(entity.getId());
        update.setConfigValue(entity.getConfigValue());
        update.setDescription(entity.getDescription());
        update.setIsEncrypted(entity.getIsEncrypted());
        // 只允许修改上面几项，其余字段不允许修改
        return configMapper.updateById(update) > 0;
    }

    @CacheEvict(key = "#result.configKey", condition = "#result != null")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Config delete(Long id) {
        Config entity = this.configMapper.selectById(id);
        if (entity == null) {
            return null;
        }
        Assert.isFalse(entity.getIsSystem() == Const.YES_TRUE, () -> new BizException(ErrorCode.SYSTEM_PARAM_CAN_NOT_DELETE));
        configMapper.deleteById(id);
        entity.setIsDeleted(Const.YES_TRUE);
        return entity;
    }

    @Override
    public PageData<Config> queryPage(PageQuery<BaseQueryParams> pageQuery) {
        return this.configMapper.queryPage(pageQuery);
    }

    @Override
    public List<Config> listAll() {
        return this.configMapper.selectList();
    }

    @CacheEvict(key = "#key")
    @Override
    public void clearCache(String key) {
        log.info("清理配置缓存：{}", key);
    }

    @CacheEvict(allEntries = true)
    @Override
    public void clearAllCache() {
        log.info("清理配置全部缓存");
    }
}
