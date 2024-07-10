package com.zhangdp.seed.service.sys;

import com.zhangdp.seed.entity.sys.SysRole;

import java.util.List;

/**
 * 2023/4/3 角色service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysRoleService {

    /**
     * 获取某个用户的角色列表
     *
     * @param userId
     * @return
     */
    List<SysRole> listUserRoles(Long userId);

    /**
     * 根据角色标识获取
     *
     * @param code
     * @return
     */
    SysRole getByCode(String code);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(SysRole entity);
}
