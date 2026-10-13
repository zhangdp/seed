package io.github.seed.module.captcha.store;

import io.github.seed.module.captcha.data.CaptchaConst;
import io.github.seed.module.captcha.data.CaptchaType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 验证码redis读写：多节点共享同一份验证码，换台机器也不会校验失败
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisCaptchaStore implements CaptchaStore {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveCode(CaptchaType type, String scene, String key, String code, Duration expire) {
        String codeKey = this.codeKey(type, scene, key);
        this.stringRedisTemplate.opsForValue().set(codeKey, code, expire);
        log.debug("保存验证码：key={}", codeKey);
    }

    @Override
    public String getCode(CaptchaType type, String scene, String key) {
        return this.stringRedisTemplate.opsForValue().get(this.codeKey(type, scene, key));
    }

    @Override
    public void deleteCode(CaptchaType type, String scene, String key) {
        String codeKey = this.codeKey(type, scene, key);
        this.stringRedisTemplate.delete(codeKey);
        log.debug("删除验证码：key={}", codeKey);
    }

    @Override
    public Duration getCodeExpire(CaptchaType type, String scene, String key) {
        return this.expire(this.codeKey(type, scene, key));
    }

    @Override
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

    @Override
    public void deleteFail(CaptchaType type, String scene, String key) {
        this.stringRedisTemplate.delete(this.failKey(type, scene, key));
    }

    @Override
    public boolean tryLockSend(CaptchaType type, String scene, String key, Duration interval) {
        String limitKey = this.limitKey(type, scene, key);
        Boolean success = this.stringRedisTemplate.opsForValue().setIfAbsent(limitKey, "1", interval);
        log.debug("尝试占用发送锁：key={}, interval={}, success={}", limitKey, interval, success);
        return Boolean.TRUE.equals(success);
    }

    @Override
    public void releaseSendLock(CaptchaType type, String scene, String key) {
        this.stringRedisTemplate.delete(this.limitKey(type, scene, key));
    }

    @Override
    public Duration getSendLockExpire(CaptchaType type, String scene, String key) {
        return this.expire(this.limitKey(type, scene, key));
    }

    /**
     * 验证码key，格式：captcha::{type}::{scene}::{key}
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     * @return redis key
     */
    private String codeKey(CaptchaType type, String scene, String key) {
        return this.buildKey(CaptchaConst.REDIS_CODE_PREFIX, type, scene, key);
    }

    /**
     * 校验失败次数key，格式：captcha::fail::{type}::{scene}::{key}
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     * @return redis key
     */
    private String failKey(CaptchaType type, String scene, String key) {
        return this.buildKey(CaptchaConst.REDIS_FAIL_PREFIX, type, scene, key);
    }

    /**
     * 发送频率限制key，格式：captcha::limit::{type}::{scene}::{key}
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     * @return redis key
     */
    private String limitKey(CaptchaType type, String scene, String key) {
        return this.buildKey(CaptchaConst.REDIS_LIMIT_PREFIX, type, scene, key);
    }

    /**
     * 组装key，各段用{@link CaptchaConst#REDIS_SPLIT}分隔
     *
     * @param prefix key前缀
     * @param type   验证码类型
     * @param scene  场景
     * @param key    验证码key
     * @return redis key
     */
    private String buildKey(String prefix, CaptchaType type, String scene, String key) {
        return prefix + CaptchaConst.REDIS_SPLIT + type.type() + CaptchaConst.REDIS_SPLIT + scene
                + CaptchaConst.REDIS_SPLIT + key;
    }

    /**
     * 获取key剩余过期时间，key不存在或已过期时返回0
     *
     * @param key redis key
     * @return 剩余过期时间，key不存在或已过期时返回0
     */
    private Duration expire(String key) {
        Long seconds = this.stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return seconds == null || seconds <= 0 ? Duration.ZERO : Duration.ofSeconds(seconds);
    }

}
