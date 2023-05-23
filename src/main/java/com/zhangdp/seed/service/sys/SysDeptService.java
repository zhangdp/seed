package com.zhangdp.seed.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangdp.seed.entity.sys.SysDept;

/**
 * 2023/4/3 部门service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 根据判断部门是否存在
     *
     * @param id
     * @return
     */
    boolean exists(Long id);
}
