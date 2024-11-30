package io.github.seed.service.sys.impl;

import io.github.seed.entity.sys.UserRole;
import io.github.seed.mapper.sys.UserRoleMapper;
import io.github.seed.service.sys.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 2023/4/3 用户角色service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleMapper userRoleMapper;

    @Override
    public List<UserRole> listByUserId(Long userId) {
        return userRoleMapper.selectListByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(UserRole entity) {
        return userRoleMapper.insert(entity) > 0;
    }
}
