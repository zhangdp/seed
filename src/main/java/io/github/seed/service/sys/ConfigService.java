package io.github.seed.service.sys;

import io.github.seed.entity.sys.Config;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;

import java.util.List;

/**
 * 系统配置service
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
    String getValue(String key);

    /**
     * 获取配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    String getValue(String key, String defaultValue);

    /**
     * 获取short类型的配置值
     *
     * @param key
     * @return
     */
    Short getShortValue(String key);

    /**
     * 获取short类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    int getShortValue(String key, short defaultValue);

    /**
     * 获取int类型的配置值
     *
     * @param key
     * @return
     */
    Integer getIntValue(String key);

    /**
     * 获取int类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    int getIntValue(String key, int defaultValue);

    /**
     * 获取long类型的配置值
     *
     * @param key
     * @return
     */
    Long getLongValue(String key);

    /**
     * 获取bool类型的配置值
     *
     * @param key
     * @return
     */
    Boolean getBoolValue(String key);

    /**
     * 获取bool类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    boolean getBoolValue(String key, boolean defaultValue);

    /**
     * 获取char类型的配置值
     *
     * @param key
     * @return
     */
    Character getCharValue(String key);

    /**
     * 获取char类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    char getCharValue(String key, char defaultValue);

    /**
     * 获取long类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    long getLongValue(String key, long defaultValue);

    /**
     * 获取float类型的配置值
     *
     * @param key
     * @return
     */
    Float getFloatValue(String key);

    /**
     * 获取float类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    float getLongValue(String key, float defaultValue);

    /**
     * 获取double类型的配置值
     *
     * @param key
     * @return
     */
    Double getDoubleValue(String key);

    /**
     * 获取double类型的配置值，如果为空则返回默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    double getDoubleValue(String key, double defaultValue);

    /**
     * 获取json格式的配置值，并转为对应bean
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T getJsonBeanValue(String key, Class<T> clazz);

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
