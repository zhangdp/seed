package com.zhangdp.seed.service.sys.impl;

import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.SeedException;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.mapper.sys.SysUserMapper;
import com.zhangdp.seed.model.PageData;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.model.query.PageQuery;
import com.zhangdp.seed.model.query.UserQuery;
import com.zhangdp.seed.service.sys.SysDeptService;
import com.zhangdp.seed.service.sys.SysUserService;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2023/4/3 用户service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOneByUsername(username);
    }

    @Override
    public boolean existsUsername(String username) {
        // return this.baseMapper.exists(this.lambdaQuery().getWrapper().eq(SysUser::getUsername, username));
        return sysUserMapper.countByUsername(username) > 0;
    }

    @Override
    public boolean existsUsernameAndIdNot(String username, Long id) {
        // return this.baseMapper.exists(this.lambdaQuery().getWrapper().eq(SysUser::getUsername, username).ne(SysUser::getId, id));
        return sysUserMapper.countByUsernameAndIdNot(username, id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(SysUser user) {
        Assert.isFalse(this.existsUsername(user.getUsername()), () -> new SeedException(ErrorCode.USERNAME_REPEAT.code(), "账号" + user.getUsername() + "已存在"));
        if (user.getDeptId() != null) {
            Assert.isTrue(sysDeptService.exists(user.getDeptId()), () -> new SeedException(ErrorCode.USERNAME_REPEAT.code(), "所属" + user.getDeptId() + "已不存在"));
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(this.encryptPassword(user.getPassword()));
        }
        return sysUserMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysUser user) {
        Assert.isFalse(this.existsUsernameAndIdNot(user.getUsername(), user.getId()), () -> new SeedException(ErrorCode.USERNAME_REPEAT.code(), "账号" + user.getUsername() + "已存在"));
        if (user.getDeptId() != null) {
            Assert.isTrue(sysDeptService.exists(user.getDeptId()), () -> new SeedException(ErrorCode.USERNAME_REPEAT.code(), "所属" + user.getDeptId() + "已不存在"));
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(this.encryptPassword(user.getPassword()));
        }
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public PageData<UserInfo> pageQuery(PageQuery<UserQuery> pageQuery) {
        return sysUserMapper.pageQuery(pageQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return sysUserMapper.deleteById(id) > 0;
    }
}
