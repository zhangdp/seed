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
    public String getConfigValue(String key) {
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
    public String getConfigValue(String key, String defaultValue) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? defaultValue : v;
    }

    @Override
    public Short getConfigShortValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Short.valueOf(v);
    }

    @Override
    public int getConfigShortValue(String key, short defaultValue) {
        Short v = this.getConfigShortValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Integer getConfigIntValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Integer.valueOf(v);
    }

    @Override
    public int getConfigIntValue(String key, int defaultValue) {
        Integer v = this.getConfigIntValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Long getConfigLongValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Long.valueOf(v);
    }

    @Override
    public Boolean getConfigBoolValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Boolean.valueOf(v);
    }

    @Override
    public boolean getConfigBoolValue(String key, boolean defaultValue) {
        Boolean v = this.getConfigBoolValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Character getConfigCharValue(String key) {
        String v = this.getConfigValue(key);
        if (v == null || v.isEmpty()) {
            return null;
        }
        Assert.isTrue(v.length() == 1, "配置项" + key + "的值不是一个字符");
        return v.charAt(0);
    }

    @Override
    public char getConfigCharValue(String key, char defaultValue) {
        Character v = this.getConfigCharValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public long getConfigLongValue(String key, long defaultValue) {
        Long v = this.getConfigLongValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Float getConfigFloatValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Float.valueOf(v);
    }

    @Override
    public float getConfigLongValue(String key, float defaultValue) {
        Float v = this.getConfigFloatValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public Double getConfigDoubleValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Double.valueOf(v);
    }

    @Override
    public double getConfigDoubleValue(String key, double defaultValue) {
        Double v = this.getConfigDoubleValue(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public <T> T getConfigJsonBean(String key, Class<T> clazz) {
        String v = this.getConfigValue(key);
        try {
            return v == null || v.isEmpty() ? null : jsonMapper.readValue(v, clazz);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("配置项" + key + "的值不是一个合法的JSON", e);
        }
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
