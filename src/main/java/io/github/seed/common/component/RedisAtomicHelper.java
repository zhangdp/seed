package io.github.seed.common.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * 2023/6/1 redis自增帮助类
 * todo 这里是否需要缓存RedisAtomicLong对象？或者使用后是否需要关闭连接？
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@ConditionalOnBean(RedisConnectionFactory.class)
@Slf4j
public class RedisAtomicHelper {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * 获取下一个redis自增
     *
     * @param key
     * @return
     */
    public long next(String key) {
        return next(key, null);
    }

    /**
     * 获取下一个redis自增，并设置过期时间
     *
     * @param key
     * @param timeout
     * @return
     */
    public long next(String key, Duration timeout) {
        RedisAtomicLong atomic = new RedisAtomicLong(key, redisConnectionFactory);
        this.setExpire(atomic, timeout);
        long v = atomic.incrementAndGet();
        log.debug("Redis自增-{}取到下一个redis自增:{}", key, v);
        return v;
    }

    /**
     * 获取一段redis自增
     *
     * @param key
     * @param size
     * @return
     */
    public long[] nextRange(String key, int size) {
        return nextRange(key, size, null);
    }

    /**
     * 获取一段redis自增，并设置过期时间
     *
     * @param key
     * @param size
     * @param timeout
     * @return
     */
    public long[] nextRange(String key, int size, Duration timeout) {
        Assert.isTrue(size > 0, "长度不能小于1");
        RedisAtomicLong atomic = new RedisAtomicLong(key, redisConnectionFactory);
        this.setExpire(atomic, timeout);
        long[] arr = new long[size];
        long max = atomic.addAndGet(size);
        log.debug("Redis自增-{}批量自增{}次后为{}", key, size, max);
        for (int i = size; i > 0; i--) {
            arr[i - 1] = max - (size - i);
        }
        return arr;
    }

    /**
     * 设置过期时间
     *
     * @param atomic
     * @param timeout
     */
    private void setExpire(RedisAtomicLong atomic, Duration timeout) {
        if (timeout != null) {
            atomic.expire(timeout);
            log.debug("Redis自增-{}设置过期时间{}", atomic.getKey(), timeout);
        }
    }

}
