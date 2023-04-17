package com.zhangdp.seed.service.log.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.entity.log.LogLogin;
import com.zhangdp.seed.mapper.log.LogLoginMapper;
import com.zhangdp.seed.service.log.LogLoginService;
import org.springframework.stereotype.Service;

/**
 * 2023/4/17 登陆日志service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class LogLoginServiceImpl extends ServiceImpl<LogLoginMapper, LogLogin> implements LogLoginService {

}
