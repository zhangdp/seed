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
     * 新增
     *
     * @param entity
     * @return
     */
    boolean add(SmsLog entity);

    /**
     * 查询可派发的短信记录：待发送的，以及发送中但已超时（抢占节点异常中断）的
     * <br>结果按「优先级降序、主键升序」排序，高优先级先发、同优先级按入库顺序
     *
     * @param sendingStaleBefore 发送中超时阈值，updated_at早于该时间的发送中记录视为可重新领取；
     *                           由调用方按自己的超时配置计算后传入
     * @param limit              最大条数
     * @return
     */
    List<SmsLog> listDispatchable(LocalDateTime sendingStaleBefore, int limit);

    /**
     * 抢占一条短信记录用于发送：把状态从待发送（或已超时的发送中）CAS更新为发送中
     * <br>多节点并发调度时只会有一个节点抢占成功，是防止重复发送的关键
     * <br>该方法一般不需要业务代码直接调用：{@code SmsManager#dispatch}已包含抢占，
     * 是唯一的发送入口
     *
     * @param smsLog             待抢占的记录，需带上主键
     * @param sendingStaleBefore 发送中超时阈值，含义同{@link #listDispatchable}，
     *                           需与取件时用的阈值一致，否则取到件却抢不到
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
     * 统计某手机号在指定场景、指定时间之后发送的短信条数，为后续限流做准备
     *
     * @param mobile 手机号
     * @param scene  业务场景
     * @param since  起始时间，为空表示不限制
     * @return
     */
    long countByMobileAndScene(String mobile, String scene, LocalDateTime since);
}
