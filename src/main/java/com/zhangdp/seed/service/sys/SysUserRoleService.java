package com.zhangdp.seed.service.sys;

import com.zhangdp.seed.entity.sys.SysUserRole;

import java.util.List;

/**
 * 2023/4/3 用户角色service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysUserRoleService {

    /**
     * 根据用户id获取列表
     *
     * @param userId
     * @return
     */
    List<SysUserRole> listByUserId(Long userId);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(SysUserRole entity);
}
