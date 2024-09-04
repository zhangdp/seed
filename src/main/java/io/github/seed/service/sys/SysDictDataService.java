package io.github.seed.service.sys;

import io.github.seed.entity.sys.SysDictData;

import java.util.List;

/**
 * 2023/4/12 字典数据service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysDictDataService {

    /**
     * 根据字典id获取字典项列表
     *
     * @param dictId
     * @return
     */
    List<SysDictData> listByDictId(Long dictId);

}
