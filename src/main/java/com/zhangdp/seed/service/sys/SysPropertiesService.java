package com.zhangdp.seed.service.sys;

import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.entity.sys.SysProperties;
import com.zhangdp.seed.model.params.BaseQueryParams;
import com.zhangdp.seed.model.params.PageQuery;

/**
 * 2023/4/12 系统参数service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysPropertiesService {

    /**
     * 根据标识获取
     *
     * @param code
     * @return
     */
    SysProperties getByCode(String code);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(SysProperties entity);

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    boolean update(SysProperties entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean delete(Long id);

    /**
     * 分页
     *
     * @param pageQuery
     * @return
     */
    PageInfo<SysProperties> pageQuery(PageQuery<BaseQueryParams> pageQuery);
}
