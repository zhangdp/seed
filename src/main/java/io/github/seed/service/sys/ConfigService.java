package io.github.seed.service.sys;

import io.github.seed.entity.sys.Config;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;

import java.util.List;

/**
 * 2023/4/12 系统参数service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface ConfigService {

    /**
     * 根据key获取
     *
     * @param key
     * @return
     */
    Config getByKey(String key);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(Config entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean update(Config entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    Config delete(Long id);

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    PageData<Config> queryPage(PageQuery<BaseQueryParams> pageQuery);

    /**
     * 获取所有列表
     *
     * @return
     */
    List<Config> listAll();

    /**
     * 获取配置值
     *
     * @param key
     * @return
     */
    String getConfigValue(String key);

    /**
     * 获取配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    String getConfigValue(String key, String defaultValue);

    /**
     * 获取short类型的配置值
     *
     * @param key
     * @return
     */
    Short getConfigShortValue(String key);

    /**
     * 获取short类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    int getConfigShortValue(String key, short defaultValue);

    /**
     * 获取int类型的配置值
     *
     * @param key
     * @return
     */
    Integer getConfigIntValue(String key);

    /**
     * 获取int类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    int getConfigIntValue(String key, int defaultValue);

    /**
     * 获取long类型的配置值
     *
     * @param key
     * @return
     */
    Long getConfigLongValue(String key);

    /**
     * 获取bool类型的配置值
     *
     * @param key
     * @return
     */
    Boolean getConfigBoolValue(String key);

    /**
     * 获取bool类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    boolean getConfigBoolValue(String key, boolean defaultValue);

    /**
     * 获取char类型的配置值
     *
     * @param key
     * @return
     */
    Character getConfigCharValue(String key);

    /**
     * 获取char类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    char getConfigCharValue(String key, char defaultValue);

    /**
     * 获取long类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    long getConfigLongValue(String key, long defaultValue);

    /**
     * 获取float类型的配置值
     *
     * @param key
     * @return
     */
    Float getConfigFloatValue(String key);

    /**
     * 获取float类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    float getConfigLongValue(String key, float defaultValue);

    /**
     * 获取double类型的配置值
     *
     * @param key
     * @return
     */
    Double getConfigDoubleValue(String key);

    /**
     * 获取double类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    double getConfigDoubleValue(String key, double defaultValue);

    /**
     * 获取json格式的配置值，并转为对应bean
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getConfigJsonBean(String key, Class<T> clazz);

    /**
     * 清理某个key的缓存
     *
     * @param key
     */
    void clearCache(String key);

    /**
     * 清理全部缓存
     */
    void clearAllCache();

}
