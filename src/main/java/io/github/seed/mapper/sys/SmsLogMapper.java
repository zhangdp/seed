package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
import io.github.seed.module.sms.data.SmsStatus;
import io.github.seed.module.sms.data.SmsResult;
import io.github.seed.entity.sys.SmsLog;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 短信日志mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface SmsLogMapper extends BaseMapper<SmsLog> {

    /**
     * 服务商返回描述的最大存储长度，超出会被截断
     */
    int MAX_RESULT_MESSAGE_LENGTH = 512;
    /**
     * 可领取（可派发/可抢占）的条件：待发送，或发送中但已超时（抢占节点异常中断）
     * <br>参数顺序：待发送状态、发送中状态、发送中超时阈值
     */
    String CLAIMABLE_CONDITION = "status = ? or (status = ? and updated_at < ?)";

    /**
     * 查询可派发的短信记录：待发送的，以及发送中但已超时（抢占节点异常中断）的
     *
     * @param sendingStaleBefore 发送中超时阈值，updated_at早于该时间的发送中记录视为可重新领取
     * @param limit              最大条数
     * @return
     */
    default List<SmsLog> listDispatchable(LocalDateTime sendingStaleBefore, int limit) {
        return this.selectListByQuery(QueryWrapper.create()
                .where(CLAIMABLE_CONDITION, SmsStatus.PENDING.status(), SmsStatus.SENDING.status(), sendingStaleBefore)
                .orderBy(SmsLog::getId, true)
                .limit(limit <= 0 ? 1 : limit));
    }

    /**
     * 抢占一条短信记录用于发送：把状态从待发送（或已超时的发送中）CAS更新为发送中
     * <br>多节点并发调度时，只有把状态成功改为发送中的那个节点会拿到记录（返回1），其余节点返回0后跳过，
     * 因此同一记录不会被重复发送；节点在发送过程中宕机时记录会停留在发送中，
     * 超过超时阈值后可被其他节点重新领取
     *
     * @param id                 记录主键
     * @param sendingStaleBefore 发送中超时阈值
     * @return 受影响行数，1表示抢占成功，0表示已被其他节点抢占或已处于终态
     */
    default int claim(Long id, LocalDateTime sendingStaleBefore) {
        SmsLog update = new SmsLog();
        update.setStatus(SmsStatus.SENDING.status());
        update.setUpdatedAt(LocalDateTime.now());
        return this.updateByQuery(update, QueryWrapper.create()
                .where("id = ? and (" + CLAIMABLE_CONDITION + ")",
                        id, SmsStatus.PENDING.status(), SmsStatus.SENDING.status(), sendingStaleBefore));
    }

    /**
     * 回写发送结果：状态、失败次数、发送时间与服务商返回信息
     * <br>按业务编号更新，因此不依赖新增时数据库生成的主键
     *
     * @param smsNo      短信业务编号
     * @param status     发送状态
     * @param retryCount 累计发送失败次数
     * @param sendAt     最近一次发送时间
     * @param result     发送结果，可为空
     * @return 受影响行数，为0说明记录不存在
     */
    default int updateSendResult(String smsNo, SmsStatus status, int retryCount, LocalDateTime sendAt,
                                 SmsResult result) {
        SmsLog update = new SmsLog();
        update.setStatus(status.status());
        update.setRetryCount(retryCount);
        update.setSendAt(sendAt);
        if (result != null) {
            update.setRequestId(result.getRequestId());
            update.setBizId(result.getBizId());
            update.setResultCode(result.getCode());
            update.setResultMessage(truncate(result.getMessage(), MAX_RESULT_MESSAGE_LENGTH));
        }
        update.setUpdatedAt(LocalDateTime.now());
        // 只更新非空字段
        return this.updateByQuery(update, QueryWrapper.create().eq(SmsLog::getSmsNo, smsNo));
    }

    /**
     * 统计某手机号在指定场景、指定时间之后发送的短信条数
     * <br>为后续基于数据库做发送频率限制与统计预留
     *
     * @param mobile 手机号，为空表示不限
     * @param scene  业务场景，为空表示不限
     * @param since  起始时间，为空表示不限
     * @return
     */
    default long countByMobileAndScene(String mobile, String scene, LocalDateTime since) {
        QueryWrapper wrapper = QueryWrapper.create();
        // 逐项判断后追加，避免空值被拼进条件
        if (mobile != null) {
            wrapper.eq(SmsLog::getMobile, mobile);
        }
        if (scene != null) {
            wrapper.eq(SmsLog::getScene, scene);
        }
        if (since != null) {
            wrapper.ge(SmsLog::getCreatedAt, since);
        }
        return this.selectCountByQuery(wrapper);
    }

    /**
     * 截断超长字符串
     * <br>私有方法不会被MyBatis解析为statement
     *
     * @param value     原字符串
     * @param maxLength 最大长度
     * @return
     */
    private static String truncate(String value, int maxLength) {
        return value != null && value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

}
