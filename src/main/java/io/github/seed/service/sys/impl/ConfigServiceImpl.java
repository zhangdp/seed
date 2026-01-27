package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.lang.Assert;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.util.SpringContextHolder;
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
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * 系统配置service实现
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
    private final JsonMapper jsonMapper;

    @Cacheable(key = "#key")
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
        Config bean = this.configMapper.selectOneById(entity.getId());
        Assert.notNull(bean, () -> new BizException(ErrorCode.PARAM_NOT_FOUND));
        Config update = new Config();
        update.setId(entity.getId());
        update.setConfigValue(entity.getConfigValue());
        update.setDescription(entity.getDescription());
        update.setIsEncrypted(entity.getIsEncrypted());
        // 只允许修改上面几项，其余字段不允许修改
        return configMapper.update(update) > 0;
    }

    @CacheEvict(key = "#result.configKey", condition = "#result != null")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Config delete(Long id) {
        Config entity = this.configMapper.selectOneById(id);
        if (entity == null) {
            return null;
        }
        Assert.isFalse(entity.getIsSystem() == Const.YES_TRUE, () -> new BizException(ErrorCode.SYSTEM_PARAM_CAN_NOT_DELETE));
        configMapper.deleteById(id);
        entity.setDeleted(Const.YES_TRUE);
        return entity;
    }

    @Override
    public PageData<Config> queryPage(PageQuery<BaseQueryParams> pageQuery) {
        return this.configMapper.queryPage(pageQuery);
    }

    @Override
    public List<Config> listAll() {
        return this.configMapper.selectAll();
    }

    @Override
    public String getValue(String key) {
        // this自调用无法生效springboot @Cacheable注解
        ConfigService _this = SpringContextHolder.getBean(ConfigService.class);
        Config config = _this.getByKey(key);
        if (config == null) {
            return null;
        }
        // todo 若配置项是加密的，这里可以做解密
        return config.getConfigValue();
    }

    @Override
    public String getValue(String key, String defaultValue) {
        String v = this.getValue(key);
        return v == null || v.isEmpty() ? defaultValue : v;
    }

    @Override
    public Short getShortValue(String key) {
        String v = this.getValue(key);
        return v == null || v.isEmpty() ? null : Short.valueOf(v);
    }

    @Override
    public int getShortValue(String key, short defaultValue) {
        Short v = this.getShortValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Integer getIntValue(String key) {
        String v = this.getValue(key);
        return v == null || v.isEmpty() ? null : Integer.valueOf(v);
    }

    @Override
    public int getIntValue(String key, int defaultValue) {
        Integer v = this.getIntValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Long getLongValue(String key) {
        String v = this.getValue(key);
        return v == null || v.isEmpty() ? null : Long.valueOf(v);
    }

    @Override
    public Boolean getBoolValue(String key) {
        String v = this.getValue(key);
        return v == null || v.isEmpty() ? null : Boolean.valueOf(v);
    }

    @Override
    public boolean getBoolValue(String key, boolean defaultValue) {
        Boolean v = this.getBoolValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Character getCharValue(String key) {
        String v = this.getValue(key);
        if (v == null || v.isEmpty()) {
            return null;
        }
        Assert.isTrue(v.length() == 1, "配置项" + key + "的值不是一个字符");
        return v.charAt(0);
    }

    @Override
    public char getCharValue(String key, char defaultValue) {
        Character v = this.getCharValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public long getLongValue(String key, long defaultValue) {
        Long v = this.getLongValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Float getFloatValue(String key) {
        String v = this.getValue(key);
        return v == null || v.isEmpty() ? null : Float.valueOf(v);
    }

    @Override
    public float getLongValue(String key, float defaultValue) {
        Float v = this.getFloatValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Double getDoubleValue(String key) {
        String v = this.getValue(key);
        return v == null || v.isEmpty() ? null : Double.valueOf(v);
    }

    @Override
    public double getDoubleValue(String key, double defaultValue) {
        Double v = this.getDoubleValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public <T> T getJsonBeanValue(String key, Class<T> clazz) {
        String v = this.getValue(key);
        return v == null || v.isEmpty() ? null : jsonMapper.readValue(v, clazz);
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
