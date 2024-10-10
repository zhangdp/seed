package io.github.seed.service.sys;

import io.github.seed.entity.sys.Dict;

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
}
