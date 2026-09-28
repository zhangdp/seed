package io.github.seed.service.sys.impl;

import io.github.seed.module.sms.data.SmsStatus;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.module.sms.data.SmsResult;
import io.github.seed.entity.sys.SmsLog;
import io.github.seed.mapper.sys.SmsLogMapper;
import io.github.seed.service.sys.SmsLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 短信日志service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsLogServiceImpl implements SmsLogService {

    private final SmsLogMapper smsLogMapper;
    private final SmsProperties smsProperties;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean add(SmsLog entity) {
        return smsLogMapper.insert(entity) > 0;
    }

    @Override
    public List<SmsLog> listDispatchable(int limit) {
        return smsLogMapper.listDispatchable(smsProperties.sendingStaleBefore(), limit);
    }

    @Override
    public boolean claim(SmsLog smsLog) {
        int rows = smsLogMapper.claim(smsLog.getId(), smsProperties.sendingStaleBefore());
        if (rows > 0) {
            // 抢占成功，同步到内存对象，避免后续判断仍用旧状态
            smsLog.setStatus(SmsStatus.SENDING.status());
        }
        return rows > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateSendResult(String smsNo, SmsStatus status, int retryCount, LocalDateTime sendAt, SmsResult result) {
        return smsLogMapper.updateSendResult(smsNo, status, retryCount, sendAt, result);
    }

    @Override
    public long countByMobileAndScene(String mobile, String scene, LocalDateTime since) {
        return smsLogMapper.countByMobileAndScene(mobile, scene, since);
    }
}
