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
     * 查询可派发的记录：待发送的，以及发送中但已超时的
     * <br>排序：优先级降序、主键升序，同优先级按入库顺序，避免老记录被新记录无限挤后
     *
     * @param sendingStaleBefore 发送中超时阈值，updated_at早于该时间的发送中记录视为可重新领取
     */
    default List<SmsLog> listDispatchable(LocalDateTime sendingStaleBefore, int limit) {
        return this.selectListByQuery(QueryWrapper.create()
                .where(CLAIMABLE_CONDITION, SmsStatus.PENDING.status(), SmsStatus.SENDING.status(), sendingStaleBefore)
                .orderBy(SmsLog::getPriority, false)
                .orderBy(SmsLog::getId, true)
                .limit(limit <= 0 ? 1 : limit));
    }

    /**
     * CAS抢占一条记录用于发送：把状态从待发送（或已超时的发送中）改为发送中
     * <br>多节点并发时只有改成功的那个节点会拿到记录，因此同一记录不会被重复发送
     *
     * @return 1表示抢占成功，0表示已被其他节点抢占或已处于终态
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
     * @param result 发送结果，可为空
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
     * 统计发送条数，为后续基于数据库做频率限制与统计预留；三个条件都可为空表示不限
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
     * 截断超长字符串；私有方法不会被MyBatis解析为statement
     */
    private static String truncate(String value, int maxLength) {
        return value != null && value.length() > maxLength ? value.substring(0, maxLength) : value;
    }

}
