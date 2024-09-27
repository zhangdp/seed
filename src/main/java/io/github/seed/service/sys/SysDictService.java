package io.github.seed.service.sys;

import io.github.seed.entity.sys.SysDict;

/**
 * 2023/4/12 字典service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysDictService {

    /**
     * 根据类型获取
     *
     * @param type
     * @return
     */
    SysDict getByType(String type);
}