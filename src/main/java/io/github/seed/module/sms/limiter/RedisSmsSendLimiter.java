package io.github.seed.module.sms.limiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 短信发送限流的redis实现：锁存redis，跨节点才拦得住换台机器重发
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisSmsSendLimiter implements SmsSendLimiter {

    /**
     * redis key前缀，完整格式：sms::limit::{scene}::{mobile}
     */
    private static final String LOCK_KEY_PREFIX = "sms::limit::";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean tryLock(String scene, String mobile, Duration interval) {
        String lockKey = this.lockKey(scene, mobile);
        Boolean success = this.stringRedisTemplate.opsForValue().setIfAbsent(lockKey, "1", interval);
        log.debug("尝试占用短信发送锁：key={}, interval={}, success={}", lockKey, interval, success);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void releaseLock(String scene, String mobile) {
        this.stringRedisTemplate.delete(this.lockKey(scene, mobile));
    }

    @Override
    public Duration getLockExpire(String scene, String mobile) {
        Long seconds = this.stringRedisTemplate.getExpire(this.lockKey(scene, mobile), TimeUnit.SECONDS);
        return seconds == null || seconds <= 0 ? Duration.ZERO : Duration.ofSeconds(seconds);
    }

    /**
     * 发送锁key，格式：sms::limit::{scene}::{mobile}
     *
     * @param scene  业务场景
     * @param mobile 手机号
     * @return redis key
     */
    private String lockKey(String scene, String mobile) {
        return LOCK_KEY_PREFIX + scene + "::" + mobile;
    }

}
