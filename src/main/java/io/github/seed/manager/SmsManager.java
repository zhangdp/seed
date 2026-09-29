package io.github.seed.manager;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.util.JsonUtils;
import io.github.seed.entity.sys.SmsLog;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.module.sms.data.SmsMessage;
import io.github.seed.module.sms.data.SmsResult;
import io.github.seed.module.sms.data.SmsStatus;
import io.github.seed.module.sms.sender.SmsSender;
import io.github.seed.service.sys.SmsLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 短信管理器
 * <br>业务编排层：串起「短信记录」与「短信发送器」——{@link SmsSender}只负责把一条短信交给服务商，
 * 不感知数据库；本类负责把短信内容落成{@code sys_sms_log}记录、按调度节奏驱动发送并回写结果，
 * 职责边界与{@code FileManager}（编排文件记录与{@code StorageAdapter}）一致
 * <br>短信发送抽象为「新增」与「派发」两个独立环节，新增即落库排队，派发由调度方执行：
 * <ol>
 *     <li><b>新增</b>：{@link #create(SmsMessage, String)}，生成待发送记录并同步入库，状态为待发送</li>
 *     <li><b>发送</b>：{@link #dispatch(SmsLog)}，先抢占再调用{@link SmsSender}发送，
 *     并按重试策略回写状态、失败次数与发送时间，本方法同步执行，
 *     由{@code SmsSendTask}定时任务或消息队列等调度方调用</li>
 * </ol>
 * 分开的意义在于：新增即已落库排队，发送动作完全交给调度层，
 * 后续可插入递送限流、定时投递、失败重试等策略，也可以直接基于{@code sys_sms_log}做发送量统计
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmsManager {

    private final SmsSender smsSender;
    private final SmsProperties smsProperties;
    private final SmsLogService smsLogService;

    /**
     * 新增短信：生成待发送记录并同步入库，状态为待发送
     * <br>本方法只负责新增排队，不会发送短信；发送由{@code SmsSendTask}等调度方完成
     * <br>优先级取{@link SmsMessage#getPriority()}，调度方按优先级从高到低取件发送
     *
     * @param message 短信内容
     * @param scene   业务场景，如login、reset_password
     * @return 待发送的短信记录，状态为待发送
     */
    public SmsLog create(SmsMessage message, String scene) {
        SmsLog smsLog = this.buildSmsLog(message, scene);
        // 新增短信：同步落库，记录进入待发送队列，返回时已确保入库成功
        smsLogService.add(smsLog);
        log.debug("短信记录已入库待发送：smsNo={}, mobile={}, scene={}", smsLog.getSmsNo(), smsLog.getMobile(), scene);
        return smsLog;
    }

    /**
     * 抢占并发送一条短信：<b>唯一的发送入口</b>，先CAS抢占该记录（状态改为发送中），抢占成功才发送
     * <br>把抢占放在发送内部，调度方就不可能漏掉这一步：多节点并发调度时只有抢到的节点会真正发送，
     * 其余节点拿到null后跳过，同一条记录不会被重复发送
     * <br>本方法同步执行，供{@code SmsSendTask}定时任务、消息队列消费者等调度方调用
     * <br>发送失败且失败次数未超过{@code app.sms.max-retry}时，状态退回待发送等待下轮调度重试
     * <br>已处于终态的记录不会被抢占；如确需重发，把状态重置为待发送后即可重新参与调度
     *
     * @param smsLog 待发送的短信记录，需带上主键
     * @return 发送结果；<b>null表示本条未被抢占</b>（已被其他节点领取或已处于终态），未产生发送
     */
    public SmsResult dispatch(SmsLog smsLog) {
        // 抢占：CAS把状态改为发送中，多节点并发时只有一个节点能抢到，抢不到的直接跳过不发送
        // 超时阈值与调度方取件时用的必须一致（都取自app.sms.sending-timeout），否则会出现取到件却抢不到
        if (!smsLogService.claim(smsLog, smsProperties.sendingStaleBefore())) {
            log.debug("短信记录未被抢占，跳过本次派发：smsNo={}，可能已被其他节点领取或已处于终态",
                    smsLog.getSmsNo());
            return null;
        }
        return this.send(smsLog);
    }

    /**
     * 调用发送器发送并回写结果，调用方必须先通过{@link #dispatch(SmsLog)}抢占该记录
     *
     * @param smsLog 已被当前节点抢占的短信记录
     * @return 发送结果
     */
    private SmsResult send(SmsLog smsLog) {
        LocalDateTime sendAt = LocalDateTime.now();
        // 由已入库的记录还原短信内容，再交给发送器
        SmsResult result;
        try {
            result = smsSender.send(this.toSmsMessage(smsLog));
            if (result == null) {
                result = SmsResult.fail(null, "短信发送器未返回发送结果");
            }
        } catch (Exception e) {
            // AbstractSmsSender 已保证不抛异常，此处兜底自定义实现
            log.error("调用短信发送器异常：smsNo={}, mobile={}", smsLog.getSmsNo(), smsLog.getMobile(), e);
            result = SmsResult.fail(null, e.getMessage());
        }
        int retryCount = smsLog.getRetryCount() == null ? 0 : smsLog.getRetryCount();
        SmsStatus status;
        if (result.isSuccess()) {
            status = SmsStatus.SUCCESS;
        } else {
            retryCount++;
            // 未超过最大重试次数：退回待发送，等待下轮调度重试；超过则置为终态失败
            status = retryCount > smsProperties.getMaxRetry() ? SmsStatus.FAIL : SmsStatus.PENDING;
            if (status == SmsStatus.FAIL) {
                // 彻底失败：服务商异常、号码或模板不合法等，重试已无意义，需人工介入
                log.error("短信彻底发送失败，已达最大重试次数：smsNo={}, mobile={}, scene={}, retryCount={}, message={}",
                        smsLog.getSmsNo(), smsLog.getMobile(), smsLog.getScene(), retryCount, result.getMessage());
            } else {
                log.warn("短信发送失败，已退回待发送等待重试：smsNo={}, mobile={}, retryCount={}/{}, message={}",
                        smsLog.getSmsNo(), smsLog.getMobile(), retryCount, smsProperties.getMaxRetry(),
                        result.getMessage());
            }
        }
        // 同步到记录对象，调用方持有的记录即可看到本次发送后的状态
        smsLog.setStatus(status.status());
        smsLog.setRetryCount(retryCount);
        smsLog.setSendAt(sendAt);
        try {
            int rows = smsLogService.updateSendResult(smsLog.getSmsNo(), status, retryCount, sendAt, result);
            if (rows <= 0) {
                log.warn("短信发送结果未回写到记录：smsNo={}, status={}", smsLog.getSmsNo(), status);
            }
        } catch (Exception e) {
            log.error("回写短信发送结果失败：smsNo={}, status={}", smsLog.getSmsNo(), status, e);
        }
        return result;
    }

    /**
     * 由短信内容构建待发送的短信记录
     *
     * @param message 短信内容
     * @param scene   业务场景
     * @return
     */
    private SmsLog buildSmsLog(SmsMessage message, String scene) {
        SmsLog smsLog = new SmsLog();
        // 业务编号：新增时服务端生成，发送结果按它回写，因此不依赖数据库生成的主键
        smsLog.setSmsNo(UUID.randomUUID().toString().replace("-", ""));
        smsLog.setScene(scene);
        smsLog.setMobile(message.getMobile());
        smsLog.setSignName(StrUtil.defaultIfBlank(message.getSignName(), smsProperties.getSignName()));
        smsLog.setTemplateCode(message.getTemplateCode());
        smsLog.setContent(message.getContent());
        if (!message.getTemplateParams().isEmpty()) {
            smsLog.setTemplateParams(JsonUtils.toJson(message.getTemplateParams()));
        }
        smsLog.setStatus(SmsStatus.PENDING.status());
        smsLog.setPriority(message.getPriority().priority());
        smsLog.setRetryCount(0);
        return smsLog;
    }

    /**
     * 由短信记录还原短信内容，用于发送已入库的记录（如调度发送、重发）
     *
     * @param smsLog 短信记录
     * @return
     */
    private SmsMessage toSmsMessage(SmsLog smsLog) {
        SmsMessage message;
        if (StrUtil.isBlank(smsLog.getTemplateCode())) {
            message = SmsMessage.text(smsLog.getMobile(), smsLog.getContent());
        } else {
            Map<String, Object> templateParams = null;
            if (StrUtil.isNotBlank(smsLog.getTemplateParams())) {
                templateParams = JsonUtils.fromJson(smsLog.getTemplateParams(),
                        new TypeReference<Map<String, Object>>() {
                        });
            }
            message = SmsMessage.template(smsLog.getMobile(), smsLog.getTemplateCode(), templateParams);
        }
        return message.signName(smsLog.getSignName());
    }

}
