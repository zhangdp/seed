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
