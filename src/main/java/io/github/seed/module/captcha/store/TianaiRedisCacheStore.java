package io.github.seed.module.captcha.store;

import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.AnyMap;
import io.github.seed.common.util.JsonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * tianai验证码缓存的redis实现：校验数据（滑块位置、点选坐标）跨节点共享，
 * 换台机器也能完成校验；替代官方starter自带的实现（它基于SB 2.2.7编译，调用了Spring 6已删除的API）
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class TianaiRedisCacheStore implements CacheStore {

    /**
     * 取值并删除的原子脚本：行为校验是一次性消费，get与del必须同批完成，
     * 分开写会在并发校验同一id时出现"两边都读到"的窗口
     */
    private static final RedisScript<String> GET_AND_REMOVE_SCRIPT = new DefaultRedisScript<>(
            "local res = redis.call('get', KEYS[1]) if res == nil then return nil "
                    + "else redis.call('del', KEYS[1]) return res end", String.class);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public AnyMap getCache(String key) {
        String json = this.stringRedisTemplate.opsForValue().get(key);
        return json == null ? null : JsonUtils.fromJson(json, AnyMap.class);
    }

    @Override
    public AnyMap getAndRemoveCache(String key) {
        String json = this.stringRedisTemplate.execute(GET_AND_REMOVE_SCRIPT, Collections.singletonList(key));
        return json == null ? null : JsonUtils.fromJson(json, AnyMap.class);
    }

    @Override
    public boolean setCache(String key, AnyMap data, Long expire, TimeUnit timeUnit) {
        this.stringRedisTemplate.opsForValue().set(key, JsonUtils.toJson(data), expire, timeUnit);
        return true;
    }

    @Override
    public Long incr(String key, long delta, Long expire, TimeUnit timeUnit) {
        Long value = this.stringRedisTemplate.opsForValue().increment(key, delta);
        if (expire != null) {
            this.stringRedisTemplate.expire(key, expire, timeUnit);
        }
        return value;
    }

    @Override
    public Long getLong(String key) {
        String value = this.stringRedisTemplate.opsForValue().get(key);
        return value == null ? null : Long.valueOf(value);
    }

    @Override
    public void close() {
        // redis连接由容器管理，无需释放
    }
}
