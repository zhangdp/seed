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
 * <br>短信采用「新增落库排队 -> 调度派发（内部先抢占再发送）-> 回写状态」流程，本任务即默认的调度方：
 * 定时查询可派发的记录（待发送、以及发送中但已超时），逐条交给{@link SmsManager#dispatch}，
 * 由后者完成抢占、调用发送器与回写
 * <br><b>多节点防重</b>：抢占在dispatch内部完成，是CAS更新（状态待发送/超时的发送中 -> 发送中），
 * 同一记录只会被一个节点抢到，其余节点抢占失败（返回null）直接跳过，因此多实例部署不会重复发送；
 * 若某节点在发送过程中宕机，记录停留在发送中，超过{@code app.sms.sending-timeout}后可被其他节点重新领取
 * <br>后续若改为消息队列调度，把{@code app.sms.send-enabled}置为false停掉本任务即可，
 * 消费者同样调用{@link SmsManager#dispatch}，发送入口不变
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
     * 轮询可派发的短信记录并派发
     * <br>fixedDelay：上一轮全部派发完后再间隔{@code app.sms.send-poll-interval}发起下一轮，避免任务堆积
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
                // 抢占与发送都在dispatch内部：抢占失败返回null，说明本轮已被其他节点领取
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
