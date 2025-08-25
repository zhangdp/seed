package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.lang.Assert;
import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.annotation.PublishEvent;
import io.github.seed.common.constant.EventConst;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.User;
import io.github.seed.mapper.sys.UserMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.params.PageQuery;
import io.github.seed.model.params.UserQuery;
import io.github.seed.service.sys.DeptService;
import io.github.seed.service.sys.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
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
@CacheConfig(cacheNames = TableNameConst.SYS_USER)
public class UserServiceImpl implements UserService {

    private final DeptService deptService;
    private final UserMapper userMapper;

    @Override
    @Cacheable(key = "#username", unless = "#result == null")
    public User getByUsername(String username) {
        return userMapper.selectOneByUsername(username);
    }

    @Override
    public User getByMobile(String mobile) {
        return userMapper.selectOneByMobile(mobile);
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.selectOneByEmail(email);
    }

    @Override
    public boolean existsUsername(String username) {
        // return this.baseMapper.exists(this.lambdaQuery().getWrapper().eq(SysUser::getUsername, username));
        return userMapper.countByUsername(username) > 0;
    }

    @Override
    public boolean existsUsernameAndIdNot(String username, Long id) {
        // return this.baseMapper.exists(this.lambdaQuery().getWrapper().eq(SysUser::getUsername, username).ne(SysUser::getId, id));
        return userMapper.countByUsernameAndIdNot(username, id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PublishEvent(value = EventConst.ADD_USER, condition = "#result == true")
    public boolean insert(User user) {
        Assert.isFalse(this.existsUsername(user.getUsername()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "账号" + user.getUsername() + "已存在"));
        if (user.getDeptId() != null) {
            Assert.isTrue(deptService.exists(user.getDeptId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "所属" + user.getDeptId() + "已不存在"));
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(user.getPassword());
        }
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PublishEvent(value = EventConst.UPDATE_USER, condition = "#result == true")
    public boolean update(User user) {
        Assert.isFalse(this.existsUsernameAndIdNot(user.getUsername(), user.getId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "账号" + user.getUsername() + "已存在"));
        if (user.getDeptId() != null) {
            Assert.isTrue(deptService.exists(user.getDeptId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "所属" + user.getDeptId() + "已不存在"));
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(user.getPassword());
        }
        return userMapper.updateById(user) > 0;
    }

    @Override
    public PageData<UserInfo> queryPage(PageQuery<UserQuery> pageQuery) {
        return userMapper.selectPage(pageQuery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long id) {
        return userMapper.deleteById(id) > 0;
    }

}
