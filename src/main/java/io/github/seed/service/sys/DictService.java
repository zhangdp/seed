package io.github.seed.service.sys;

import io.github.seed.entity.sys.Dict;
import io.github.seed.model.PageData;
import io.github.seed.model.query.BaseTextQuery;
import io.github.seed.model.query.PageQuery;

import java.util.List;

/**
 * 2023/4/12 字典service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface DictService {

    /**
     * 根据类型获取
     *
     * @param type
     * @return
     */
    Dict getByType(String type);

    /**
     * 分页查询
     *
     * @param pageQuery
     * @return
     */
    PageData<Dict> queryPage(PageQuery<BaseTextQuery> pageQuery);

    /**
     * 获取全部字典
     *
     * @return
     */
    List<Dict> listAll();

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(Dict entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean update(Dict entity);

    /**
     * 删除，同时删除其下的字典项
     *
     * @param id
     * @return
     */
    boolean delete(Long id);
}
