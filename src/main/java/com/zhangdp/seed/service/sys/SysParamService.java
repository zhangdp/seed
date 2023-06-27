package com.zhangdp.seed.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zhangdp.seed.entity.sys.SysParam;
import com.zhangdp.seed.model.query.BaseQueryParams;
import com.zhangdp.seed.model.query.PageQuery;

/**
 * 2023/4/12 系统参数service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysParamService extends IService<SysParam> {

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
    boolean insert(SysParam param);

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
