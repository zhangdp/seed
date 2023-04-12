package com.zhangdp.seed.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangdp.seed.entity.sys.SysParam;

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

}
