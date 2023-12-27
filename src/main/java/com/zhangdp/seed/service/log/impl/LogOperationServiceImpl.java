package com.zhangdp.seed.service.log.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangdp.seed.entity.log.LogOperation;
import com.zhangdp.seed.mapper.log.LogOperationMapper;
import com.zhangdp.seed.service.log.LogOperationService;
import org.springframework.stereotype.Service;

/**
 * 2023/4/17 操作日志service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
public class LogOperationServiceImpl extends ServiceImpl<LogOperationMapper, LogOperation> implements LogOperationService {

}
