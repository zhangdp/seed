package com.zhangdp.seed.service.sys;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhangdp.seed.entity.sys.SysUserRole;

import java.util.List;

/**
 * 2023/4/3 用户角色service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SysUserRoleService extends IService<SysUserRole> {

    /**
     * 根据用户id获取列表
     *
     * @param userId
     * @return
     */
    List<SysUserRole> listByUserId(Long userId);
}
