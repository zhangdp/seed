package io.github.seed.service.sys;

import io.github.seed.entity.sys.SysConfig;
import io.github.seed.model.PageData;
import io.github.seed.model.params.BaseQueryParams;
import io.github.seed.model.params.PageQuery;

/**
 * 2023/4/12 系统参数service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysConfigService {

    /**
     * 根据key获取
     *
     * @param key
     * @return
     */
    SysConfig getByKey(String key);

    /**
     * 根据key获取值
     *
     * @param key
     * @return
     */
    String getValue(String key);

    /**
     * 根据key获取值，如果不存在则使用传入的默认值
     *
     * @param key
     * @param defaultValue
     * @return
     */
    String getValue(String key, String defaultValue);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(SysConfig entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean update(SysConfig entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    PageData<SysConfig> queryPage(PageQuery<BaseQueryParams> pageQuery);
}
