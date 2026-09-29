package io.github.seed.module.captcha;

import io.github.seed.module.captcha.data.CaptchaConst;
import io.github.seed.module.captcha.data.CaptchaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 验证码redis读写
 * <br>统一使用{@link StringRedisTemplate}，不用项目自定义的{@code RedisTemplate<String, Object>}：
 * 后者的value序列化器会写入{@code @class}类型信息并据此反序列化，而验证码只需要存一个字符串
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CaptchaStore {

    private final StringRedisTemplate stringRedisTemplate;

    // ==================== key 组装 ====================

    /**
     * 验证码key，格式：captcha::{type}::{scene}::{key}
     */
    public String codeKey(CaptchaType type, String scene, String key) {
        return this.buildKey(CaptchaConst.REDIS_CODE_PREFIX, type, scene, key);
    }

    /**
     * 校验失败次数key，格式：captcha::fail::{type}::{scene}::{key}
     */
    public String failKey(CaptchaType type, String scene, String key) {
        return this.buildKey(CaptchaConst.REDIS_FAIL_PREFIX, type, scene, key);
    }

    /**
     * 发送频率限制key，格式：captcha::limit::{type}::{scene}::{key}
     */
    public String limitKey(CaptchaType type, String scene, String key) {
        return this.buildKey(CaptchaConst.REDIS_LIMIT_PREFIX, type, scene, key);
    }

    /**
     * 短信每日发送次数key，格式：captcha::count::sms::daily::{mobile}
     */
    public String smsDailyKey(String mobile) {
        return CaptchaConst.REDIS_SMS_DAILY_PREFIX + CaptchaConst.REDIS_SPLIT + mobile;
    }

    private String buildKey(String prefix, CaptchaType type, String scene, String key) {
        return prefix + CaptchaConst.REDIS_SPLIT + type.type() + CaptchaConst.REDIS_SPLIT + scene
                + CaptchaConst.REDIS_SPLIT + key;
    }

    // ==================== 验证码 ====================

    public void saveCode(CaptchaType type, String scene, String key, String code, Duration expire) {
        String codeKey = this.codeKey(type, scene, key);
        this.stringRedisTemplate.opsForValue().set(codeKey, code, expire);
        log.debug("保存验证码：key={}", codeKey);
    }

    /**
     * 获取验证码，不存在时返回null
     */
    public String getCode(CaptchaType type, String scene, String key) {
        return this.stringRedisTemplate.opsForValue().get(this.codeKey(type, scene, key));
    }

    public void deleteCode(CaptchaType type, String scene, String key) {
        String codeKey = this.codeKey(type, scene, key);
        this.stringRedisTemplate.delete(codeKey);
        log.debug("删除验证码：key={}", codeKey);
    }

    // ==================== 校验失败次数 ====================

    /**
     * 累加校验失败次数
     *
     * @param expire 过期时间，用于避免产生永不失效的残留key
     * @return 累加后的失败次数
     */
    public long increaseFail(CaptchaType type, String scene, String key, Duration expire) {
        String failKey = this.failKey(type, scene, key);
        Long count = this.stringRedisTemplate.opsForValue().increment(failKey);
        long value = count == null ? 1L : count;
        if (expire != null && !expire.isNegative() && !expire.isZero()) {
            // 每次自增都刷新过期时间，避免进程中断产生永不失效的残留key
            this.stringRedisTemplate.expire(failKey, expire);
        }
        return value;
    }

    public void deleteFail(CaptchaType type, String scene, String key) {
        this.stringRedisTemplate.delete(this.failKey(type, scene, key));
    }

    // ==================== 发送频率限制 ====================

    /**
     * 抢占式设置发送锁
     *
     * @return true表示抢占成功可以发送
     */
    public boolean tryLockSend(CaptchaType type, String scene, String key, Duration interval) {
        String limitKey = this.limitKey(type, scene, key);
        Boolean success = this.stringRedisTemplate.opsForValue().setIfAbsent(limitKey, "1", interval);
        log.debug("尝试占用发送锁：key={}, interval={}, success={}", limitKey, interval, success);
        return Boolean.TRUE.equals(success);
    }

    /**
     * 释放发送锁；目前只用于当日额度已达上限时，避免用户被白白锁住一个发送间隔
     * <br>短信发送失败不释放：频率限制按时间走，与是否送达无关
     */
    public void releaseSendLock(CaptchaType type, String scene, String key) {
        this.stringRedisTemplate.delete(this.limitKey(type, scene, key));
    }

    /**
     * 获取key剩余过期时间，key不存在或已过期时返回0
     */
    public Duration getExpire(String key) {
        Long seconds = this.stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return seconds == null || seconds <= 0 ? Duration.ZERO : Duration.ofSeconds(seconds);
    }

    // ==================== 短信每日发送次数 ====================

    /**
     * 累加手机号当日发送次数，首次发送时设置到当天24点过期
     *
     * @return 累加后的当日发送次数
     */
    public long increaseSmsDaily(String mobile) {
        String dailyKey = this.smsDailyKey(mobile);
        Long count = this.stringRedisTemplate.opsForValue().increment(dailyKey);
        long value = count == null ? 1L : count;
        if (value == 1L) {
            Duration ttl = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay());
            if (!ttl.isNegative() && !ttl.isZero()) {
                this.stringRedisTemplate.expire(dailyKey, ttl);
            }
        }
        return value;
    }

}
