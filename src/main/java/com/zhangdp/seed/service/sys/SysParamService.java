package com.zhangdp.seed.service.sys;

import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.entity.sys.SysParam;
import com.zhangdp.seed.model.params.BaseQueryParams;
import com.zhangdp.seed.model.params.PageQuery;

/**
 * 2023/4/12 系统参数service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysParamService {

    /**
     * 根据标识获取
     *
     * @param code
     * @return
     */
    SysParam getByCode(String code);

    /**
     * 新增
     *
     * @param param
     * @return
     */
    boolean add(SysParam param);

    /**
     * 修改
     *
     * @param param
     * @return
     */
    boolean update(SysParam param);

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
    PageInfo<SysParam> pageQuery(PageQuery<BaseQueryParams> pageQuery);
}
