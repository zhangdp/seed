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

    /**
     * 新增短信记录
     *
     * @param entity 短信记录
     * @return 是否新增成功
     */
    boolean add(SmsLog entity);

    /**
     * 查询可派发的记录：待发送的，以及发送中但已超时的；按「优先级降序、主键升序」排序
     *
     * @param sendingStaleBefore 发送中超时阈值，updated_at早于该时间的发送中记录视为可重新领取
     * @param limit              最大条数
     * @return 可派发的记录
     */
    List<SmsLog> listDispatchable(LocalDateTime sendingStaleBefore, int limit);

    /**
     * CAS抢占一条记录用于发送，多节点并发时只会有一个节点成功，是防止重复发送的关键
     * <br>业务代码一般不需要直接调用：{@code SmsManager#dispatch}已包含抢占，是唯一的发送入口
     *
     * @param smsLog             待抢占的记录，需带上主键
     * @param sendingStaleBefore 超时阈值，需与取件时用的值一致，否则会取到件却抢不到
     * @return 是否抢占成功
     */
    boolean claim(SmsLog smsLog, LocalDateTime sendingStaleBefore);

    /**
     * 回写发送结果：状态、失败次数、发送时间与服务商返回信息
     *
     * @param smsNo      短信业务编号
     * @param status     发送状态
     * @param retryCount 累计发送失败次数
     * @param sendAt     最近一次发送时间
     * @param result     发送结果，可为空
     * @return 受影响行数，为0说明记录不存在
     */
    int updateSendResult(String smsNo, SmsStatus status, int retryCount, LocalDateTime sendAt, SmsResult result);

    /**
     * 统计发送条数，为后续限流做准备；三个条件都可为空表示不限
     *
     * @param mobile 手机号，为空表示不限
     * @param scene  业务场景，为空表示不限
     * @param since  起始时间，为空表示不限
     * @return 发送条数
     */
    long countByMobileAndScene(String mobile, String scene, LocalDateTime since);
}
