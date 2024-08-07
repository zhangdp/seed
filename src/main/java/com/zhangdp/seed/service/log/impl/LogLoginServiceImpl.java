package com.zhangdp.seed.service.log.impl;

import com.zhangdp.seed.entity.log.LogLogin;
import com.zhangdp.seed.mapper.log.LogLoginMapper;
import com.zhangdp.seed.service.log.LogLoginService;
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
public class LogLoginServiceImpl implements LogLoginService {

    private final LogLoginMapper logLoginMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(LogLogin entity) {
        return this.logLoginMapper.insert(entity) > 0;
    }
}
