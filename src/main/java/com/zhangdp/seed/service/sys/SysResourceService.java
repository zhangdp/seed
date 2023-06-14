package com.zhangdp.seed.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangdp.seed.entity.sys.SysResource;

import java.util.List;

/**
 * 2023/4/12 资源service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysResourceService extends IService<SysResource> {

    /**
     * 获取某个角色拥有的资源列表
     *
     * @param roleId
     * @return
     */
    List<SysResource> listRoleResources(Long roleId);

}
