package io.github.seed.module.sms.limiter;

import java.time.Duration;

/**
 * 短信发送限流：约束「同一个业务场景下同一手机号多久能再收到一条短信」
 * <br>属于发送侧规则，因此放在sms模块而不是验证码模块——验证码只管码本身是否可用，
 * 发不发得出去由通道说了算
 * <br>实现类可以是redis、本地缓存或数据库；key怎么拼是实现细节，因此接口不暴露key
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SmsSendLimiter {

    /**
     * 抢占式占用发送锁，避免「检查-发送」竞态导致并发短信轰炸
     *
     * @param scene    业务场景，不同场景互不影响
     * @param mobile   手机号
     * @param interval 发送间隔
     * @return true表示抢占成功、可以发送
     */
    boolean tryLock(String scene, String mobile, Duration interval);

    /**
     * 释放发送锁，用于占用后因故放弃发送时归还额度
     *
     * @param scene  业务场景
     * @param mobile 手机号
     */
    void releaseLock(String scene, String mobile);

    /**
     * 获取发送锁剩余有效期，用于提示「多少秒后可再发」
     *
     * @param scene  业务场景
     * @param mobile 手机号
     * @return 剩余过期时间，不存在或已过期时返回0
     */
    Duration getLockExpire(String scene, String mobile);

}
