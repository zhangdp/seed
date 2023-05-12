package com.zhangdp.seed.service.sys.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.mapper.sys.SysUserMapper;
import com.zhangdp.seed.service.sys.SysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2023/4/3 用户service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(this.lambdaQuery().getWrapper().eq(SysUser::getUsername, username));
    }

    @Override
    public boolean existsUsername(String username) {
        return this.baseMapper.exists(this.lambdaQuery().getWrapper().eq(SysUser::getUsername, username));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(SysUser user) {
        Assert.isFalse(this.existsUsername(user.getUsername()), "账号" + user.getUsername() + "已存在");
        return this.save(user);
    }
}
