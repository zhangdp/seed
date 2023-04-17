package com.zhangdp.seed.service.log.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.entity.log.LogOperate;
import com.zhangdp.seed.mapper.log.LogOperateMapper;
import com.zhangdp.seed.service.log.LogOperateService;
import org.springframework.stereotype.Service;

/**
 * 2023/4/17 操作日志service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class LogOperateServiceImpl extends ServiceImpl<LogOperateMapper, LogOperate> implements LogOperateService {

}
