package io.github.seed.service.sys;

import io.github.seed.module.sms.data.SmsStatus;
import io.github.seed.module.sms.data.SmsResult;
import io.github.seed.entity.sys.SmsLog;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 短信日志service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SmsLogService {

    boolean add(SmsLog entity);

    /**
     * 查询可派发的记录：待发送的，以及发送中但已超时的；按「优先级降序、主键升序」排序
     *
     * @param sendingStaleBefore 发送中超时阈值，updated_at早于该时间的发送中记录视为可重新领取
     */
    List<SmsLog> listDispatchable(LocalDateTime sendingStaleBefore, int limit);

    /**
     * CAS抢占一条记录用于发送，多节点并发时只会有一个节点成功，是防止重复发送的关键
     * <br>业务代码一般不需要直接调用：{@code SmsManager#dispatch}已包含抢占，是唯一的发送入口
     *
     * @param sendingStaleBefore 超时阈值，需与取件时用的值一致，否则会取到件却抢不到
     */
    boolean claim(SmsLog smsLog, LocalDateTime sendingStaleBefore);

    /**
     * 回写发送结果：状态、失败次数、发送时间与服务商返回信息
     *
     * @param result 发送结果，可为空
     * @return 受影响行数，为0说明记录不存在
     */
    int updateSendResult(String smsNo, SmsStatus status, int retryCount, LocalDateTime sendAt, SmsResult result);

    /**
     * 统计发送条数，为后续限流做准备；三个条件都可为空表示不限
     */
    long countByMobileAndScene(String mobile, String scene, LocalDateTime since);
}
