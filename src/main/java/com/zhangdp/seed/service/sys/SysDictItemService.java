package com.zhangdp.seed.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangdp.seed.entity.sys.SysDictItem;

import java.util.List;

/**
 * 2023/4/12 字典项service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysDictItemService extends IService<SysDictItem> {

    /**
     * 根据字典id获取字典项列表
     *
     * @param dictId
     * @return
     */
    List<SysDictItem> listByDictId(Long dictId);

}
