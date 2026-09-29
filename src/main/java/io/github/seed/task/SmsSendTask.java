package io.github.seed.task;

import io.github.seed.entity.sys.SmsLog;
import io.github.seed.manager.SmsManager;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.module.sms.data.SmsResult;
import io.github.seed.service.sys.SmsLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * 短信定时派发任务
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SmsSendTask {

    private final SmsManager smsManager;
    private final SmsLogService smsLogService;
    private final SmsProperties smsProperties;

    /**
     * 轮询并派发；上一轮派发完后再间隔{@code app.sms.send-poll-interval}发起下一轮
     */
    @Scheduled(fixedDelayString = "${app.sms.send-poll-interval:5000}", initialDelay = 10000L)
    public void dispatchPending() {
        List<SmsLog> dispatchable = smsLogService.listDispatchable(smsProperties.sendingStaleBefore(),
                smsProperties.getSendBatchSize());
        if (dispatchable.isEmpty()) {
            return;
        }
        log.info("短信派发任务开始：本轮可派发{}条", dispatchable.size());
        int success = 0;
        int skipped = 0;
        for (SmsLog smsLog : dispatchable) {
            try {
                // 抢占失败返回null，说明本轮已被其他节点领取
                SmsResult result = smsManager.dispatch(smsLog);
                if (result == null) {
                    skipped++;
                    continue;
                }
                if (result.isSuccess()) {
                    success++;
                }
            } catch (Exception e) {
                // SmsManager.dispatch已兜底不抛异常，此处防调度任务被意外异常中断
                log.error("短信派发异常：smsNo={}, mobile={}", smsLog.getSmsNo(), smsLog.getMobile(), e);
            }
        }
        log.info("短信派发任务结束：可派发{}条，成功{}条，失败{}条，被其他节点抢占跳过{}条",
                dispatchable.size(), success, dispatchable.size() - success - skipped, skipped);
    }

}
