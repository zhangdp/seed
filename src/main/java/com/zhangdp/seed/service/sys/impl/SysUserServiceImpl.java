package com.zhangdp.seed.service.sys.impl;

import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.entity.sys.SysUser;
import com.zhangdp.seed.mapper.sys.SysUserMapper;
import com.zhangdp.seed.model.PageData;
import com.zhangdp.seed.model.dto.UserInfo;
import com.zhangdp.seed.model.params.PageQuery;
import com.zhangdp.seed.model.params.UserQuery;
import com.zhangdp.seed.service.sys.SysDeptService;
import com.zhangdp.seed.service.sys.SysUserService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2023/4/3 用户service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysDeptService sysDeptService;
    private final SysUserMapper sysUserMapper;

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.selectOneByUsername(username);
    }

    @Override
    public SysUser getByMobile(String mobile) {
        return sysUserMapper.selectOneByMobile(mobile);
    }

    @Override
    public SysUser getByEmail(String email) {
        return sysUserMapper.selectOneByEmail(email);
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
        Assert.isFalse(this.existsUsername(user.getUsername()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "账号" + user.getUsername() + "已存在"));
        if (user.getDeptId() != null) {
            Assert.isTrue(sysDeptService.exists(user.getDeptId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "所属" + user.getDeptId() + "已不存在"));
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(user.getPassword());
        }
        return sysUserMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysUser user) {
        Assert.isFalse(this.existsUsernameAndIdNot(user.getUsername(), user.getId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "账号" + user.getUsername() + "已存在"));
        if (user.getDeptId() != null) {
            Assert.isTrue(sysDeptService.exists(user.getDeptId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "所属" + user.getDeptId() + "已不存在"));
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(user.getPassword());
        }
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public PageData<UserInfo> queryPage(PageQuery<UserQuery> pageQuery) {
        return sysUserMapper.queryPage(pageQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return sysUserMapper.deleteById(id) > 0;
    }

}
