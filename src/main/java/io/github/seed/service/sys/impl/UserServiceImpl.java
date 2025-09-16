package io.github.seed.service.sys.impl;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.lang.Assert;
import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.annotation.PublishEvent;
import io.github.seed.common.constant.EventConst;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.entity.sys.User;
import io.github.seed.entity.sys.UserRole;
import io.github.seed.mapper.sys.UserMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.dto.AddUserDto;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.params.PageQuery;
import io.github.seed.model.params.UserQuery;
import io.github.seed.service.sys.DeptService;
import io.github.seed.service.sys.RoleService;
import io.github.seed.service.sys.UserRoleService;
import io.github.seed.service.sys.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

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
        return userMapper.existsByUsername(username);
    }

    @Override
    public boolean existsUsernameAndIdNot(String username, Long id) {
        return userMapper.existsByUsernameAndIdNot(username, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PublishEvent(value = EventConst.ADD_USER, condition = "#result == true")
    public boolean add(AddUserDto user) {
        Assert.isFalse(this.existsUsername(user.getUsername()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "账号" + user.getUsername() + "已存在"));
        if (user.getDeptId() != null) {
            Assert.isTrue(deptService.exists(user.getDeptId()), () -> new BizException(ErrorCode.DEPT_NOT_EXISTS.code(), "对应部门" + user.getDeptId() + "已不存在"));
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        if (CollUtil.isNotEmpty(user.getRoleIds())) {
            List<UserRole> userRoles = new ArrayList<>();
            for (Long roleId : user.getRoleIds()) {
                Assert.isTrue(roleService.exists(roleId), () -> new BizException(ErrorCode.ROLE_NOT_EXISTS.code(), "对应角色" + roleId + "已不存在"));
                UserRole ur = new UserRole();
                ur.setRoleId(roleId);
                ur.setUserId(user.getId());
                userRoles.add(ur);
            }
            userRoleService.addBatch(userRoles);
        }
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PublishEvent(value = EventConst.UPDATE_USER, condition = "#result == true")
    public boolean update(AddUserDto user) {
        Assert.isFalse(this.existsUsernameAndIdNot(user.getUsername(), user.getId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "账号" + user.getUsername() + "已存在"));
        if (user.getDeptId() != null) {
            Assert.isTrue(deptService.exists(user.getDeptId()), () -> new BizException(ErrorCode.USERNAME_REPEAT.code(), "所属" + user.getDeptId() + "已不存在"));
        }
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(user.getPassword());
        }
        return userMapper.update(user) > 0;
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
