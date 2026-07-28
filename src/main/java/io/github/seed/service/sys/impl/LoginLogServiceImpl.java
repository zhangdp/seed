package io.github.seed.service.sys.impl;

import io.github.seed.entity.sys.LoginLog;
import io.github.seed.mapper.sys.LoginLogMapper;
import io.github.seed.model.PageData;
import io.github.seed.model.params.LoginLogQuery;
import io.github.seed.model.params.PageQuery;
import io.github.seed.service.sys.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2023/4/17 登录日志service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

    private final LoginLogMapper loginLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(LoginLog entity) {
        return this.loginLogMapper.insert(entity) > 0;
    }

    @Override
    public PageData<LoginLog> queryPage(PageQuery<LoginLogQuery> pageQuery) {
        return loginLogMapper.selectPage(pageQuery);
    }
}
