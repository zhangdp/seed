package com.zhangdp.seed.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangdp.seed.entity.sys.SysRoleResource;

import java.util.Collection;
import java.util.List;

/**
 * 2023/4/12 角色关联资源service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysRoleResourceService extends IService<SysRoleResource> {

    /**
     * 根据角色id获取资源列表
     *
     * @param roleId
     * @return
     */
    List<SysRoleResource> listByRoleId(Long roleId);

    /**
     * 根据角色id列表获取资源列表
     *
     * @param roleIds
     * @return
     */
    List<SysRoleResource> listByRoleIds(Collection<Long> roleIds);
}
