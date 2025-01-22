package io.github.seed.common.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.entity.sys.Config;
import io.github.seed.service.sys.ConfigService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.cache.impl.TimedCache;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.stereotype.Component;

/**
 * 2024/12/5 带缓存的系统配置帮助类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class CacheableConfigHelper {

    private static final long TIMEOUT = 60000L;

    private final TimedCache<String, Config> localCache = new TimedCache<>(TIMEOUT);

    private final ConfigService configService;
    private final ObjectMapper objectMapper;

    /**
     * 获取配置，会先从本地内存中缓存获取（缓存过期时间默认10秒）
     *
     * @param key
     * @return
     */
    public Config getConfig(String key) {
        Config config = localCache.get(key, false);
        if (config == null) {
            config = configService.getByKey(key);
            localCache.put(key, config);
        }
        return config;
    }

    /**
     * 获取配置值
     *
     * @param key
     * @return
     */
    public String getConfigValue(String key) {
        Config config = localCache.get(key, false);
        if (config == null) {
            return null;
        }
        // todo 若配置项是加密的，这里可以做解密
        return config.getConfigValue();
    }

    /**
     * 获取配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getConfigValue(String key, String defaultValue) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? defaultValue : v;
    }

    /**
     * 获取short类型的配置值
     *
     * @param key
     * @return
     */
    public Short getConfigShortValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Short.valueOf(v);
    }

    /**
     * 获取short类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getConfigShortValue(String key, short defaultValue) {
        Short v = this.getConfigShortValue(key);
        return v == null ? defaultValue : v;
    }

    /**
     * 获取int类型的配置值
     *
     * @param key
     * @return
     */
    public Integer getConfigIntValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Integer.valueOf(v);
    }

    /**
     * 获取int类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public int getConfigIntValue(String key, int defaultValue) {
        Integer v = this.getConfigIntValue(key);
        return v == null ? defaultValue : v;
    }

    /**
     * 获取long类型的配置值
     *
     * @param key
     * @return
     */
    public Long getConfigLongValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Long.valueOf(v);
    }

    /**
     * 获取bool类型的配置值
     *
     * @param key
     * @return
     */
    public Boolean getConfigBoolValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Boolean.valueOf(v);
    }

    /**
     * 获取bool类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getConfigBoolValue(String key, boolean defaultValue) {
        Boolean v = this.getConfigBoolValue(key);
        return v == null ? defaultValue : v;
    }

    /**
     * 获取char类型的配置值
     *
     * @param key
     * @return
     */
    public Character getConfigCharValue(String key) {
        String v = this.getConfigValue(key);
        if (v == null || v.isEmpty()) {
            return null;
        }
        Assert.isTrue(v.length() == 1, "配置项" + key + "的值不是一个字符");
        return v.charAt(0);
    }

    /**
     * 获取char类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public char getConfigBooleanValue(String key, char defaultValue) {
        Character v = this.getConfigCharValue(key);
        return v == null ? defaultValue : v;
    }

    /**
     * 获取long类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public long getConfigLongValue(String key, long defaultValue) {
        Long v = this.getConfigLongValue(key);
        return v == null ? defaultValue : v;
    }

    /**
     * 获取float类型的配置值
     *
     * @param key
     * @return
     */
    public Float getConfigFloatValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Float.valueOf(v);
    }

    /**
     * 获取float类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public float getConfigLongValue(String key, float defaultValue) {
        Float v = this.getConfigFloatValue(key);
        return v == null ? defaultValue : v;
    }

    /**
     * 获取double类型的配置值
     *
     * @param key
     * @return
     */
    public Double getConfigDoubleValue(String key) {
        String v = this.getConfigValue(key);
        return v == null || v.isEmpty() ? null : Double.valueOf(v);
    }

    /**
     * 获取double类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public double getConfigDoubleValue(String key, double defaultValue) {
        Double v = this.getConfigDoubleValue(key);
        return v == null ? defaultValue : v;
    }

    /**
     * 获取json格式的配置值，并转为对应bean
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getConfigJsonBean(String key, Class<T> clazz) {
        String v = this.getConfigValue(key);
        try {
            return v == null || v.isEmpty() ? null : objectMapper.readValue(v, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("配置项" + key + "的值不是一个json格式", e);
        }
    }

    /**
     * 清理缓存中过期对象
     *
     */
    public void pruneCache() {
        localCache.prune();
    }

    /**
     * 清理某个key的缓存
     *
     * @param key
     */
    public void clearCache(String key) {
        localCache.remove(key);
        this.configService.clearCache(key);
    }

    /**
     * 清空全部缓存
     *
     */
    public void clearAllCache() {
        localCache.clear();
        this.configService.clearAllCache();
    }

}
