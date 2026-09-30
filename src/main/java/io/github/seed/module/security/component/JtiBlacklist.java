package io.github.seed.module.security.component;

import io.github.seed.module.security.data.SecurityConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.TaskScheduler;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * jti黑名单：已登出、被踢下线的令牌在剩余有效期内仍然自带合法签名，只能靠拉黑挡住
 * <br>判定走节点内存，不查redis；redis只当持久化与跨节点同步的介质：
 * zset存「jti -> 拉黑到期时间戳」，条目到期即等于自动解除，无需额外删除
 * <br>跨节点同步发的是版本号而不是明细：pub/sub不持久化，丢一条明细会导致节点间永久不一致，
 * 版本号落后就全量重拉，怎么丢都能自愈
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class JtiBlacklist implements InitializingBean {

    /**
     * 兜底对齐间隔：pub/sub断线期间的通知收不到，靠它纠偏
     */
    private static final Duration ALIGN_INTERVAL = Duration.ofMinutes(1);

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer listenerContainer;
    private final TaskScheduler taskScheduler;

    /**
     * 内存黑名单：jti -> 拉黑到期时间戳（毫秒）
     */
    private final Map<String, Long> blacklist = new ConcurrentHashMap<>();
    /**
     * 内存数据的版本号，与redis里的版本号比对判断是否落后
     */
    private volatile long version = -1L;

    @Override
    public void afterPropertiesSet() {
        this.refresh();
        MessageListenerAdapter adapter = new MessageListenerAdapter(this, "onVersionChanged");
        // 广播的是纯数字版本号，序列化方式必须和发送端（StringRedisTemplate）一致
        adapter.setSerializer(RedisSerializer.string());
        adapter.afterPropertiesSet();
        this.listenerContainer.addMessageListener(adapter, new ChannelTopic(SecurityConst.REDIS_JTI_BLACKLIST_CHANNEL));
        this.taskScheduler.scheduleAtFixedRate(this::refreshIfStale, ALIGN_INTERVAL);
        log.info("jti黑名单已预热，条目数：{}，版本号：{}", this.blacklist.size(), this.version);
    }

    /**
     * 拉黑单个jti
     *
     * @param jti      令牌唯一标识
     * @param expireAt 拉黑到期时间戳（毫秒），取令牌自身的过期时间即可，到期后无需再记
     */
    public void blacklist(String jti, long expireAt) {
        this.blacklist(Map.of(jti, expireAt));
    }

    /**
     * 批量拉黑，踢人时一个用户的多个令牌一次写进去
     *
     * @param items jti与该令牌的过期时间戳（毫秒）
     */
    public void blacklist(Map<String, Long> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        Set<ZSetOperations.TypedTuple<String>> tuples = items.entrySet().stream()
                .map(e -> ZSetOperations.TypedTuple.of(e.getKey(), (double) e.getValue()))
                .collect(Collectors.toSet());
        this.stringRedisTemplate.opsForZSet().add(SecurityConst.REDIS_JTI_BLACKLIST, tuples);
        // 先更新本地再广播：本节点此刻就该生效，不能等消息绕一圈回来
        this.blacklist.putAll(items);
        long newVersion = this.incrVersion();
        this.version = newVersion;
        this.stringRedisTemplate.convertAndSend(SecurityConst.REDIS_JTI_BLACKLIST_CHANNEL, String.valueOf(newVersion));
        if (log.isDebugEnabled()) {
            log.debug("拉黑jti，数量：{}，版本号：{}", items.size(), newVersion);
        }
    }

    /**
     * 是否被拉黑；已过期的条目顺手清掉，视为未拉黑
     *
     * @param jti 令牌唯一标识
     * @return true表示已拉黑，该令牌不可用
     */
    public boolean isBlacklisted(String jti) {
        Long expireAt = this.blacklist.get(jti);
        if (expireAt == null) {
            return false;
        }
        if (expireAt <= System.currentTimeMillis()) {
            this.blacklist.remove(jti);
            return false;
        }
        return true;
    }

    /**
     * redis变更通知：只带版本号，落后于redis就全量重拉
     *
     * @param message 版本号字符串
     */
    public void onVersionChanged(String message) {
        long remote = this.parseVersion(message);
        if (remote > this.version) {
            this.refresh();
        }
    }

    /**
     * 版本号落后时重拉，供定时任务兜底
     */
    public void refreshIfStale() {
        if (this.readVersion() > this.version) {
            this.refresh();
        }
    }

    /**
     * 全量重拉：只拉未过期的条目，已过期的留在redis里由清理任务处理
     */
    public void refresh() {
        long now = System.currentTimeMillis();
        Set<ZSetOperations.TypedTuple<String>> tuples =
                this.stringRedisTemplate.opsForZSet().rangeByScoreWithScores(SecurityConst.REDIS_JTI_BLACKLIST, now, Double.MAX_VALUE);
        Map<String, Long> fresh = new HashMap<>();
        if (tuples != null) {
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                if (tuple.getValue() == null || tuple.getScore() == null) {
                    continue;
                }
                fresh.put(tuple.getValue(), (long) (double) tuple.getScore());
            }
        }
        this.blacklist.clear();
        this.blacklist.putAll(fresh);
        this.version = this.readVersion();
        // 顺手清掉redis里已过期的条目，避免zset无限增长
        this.stringRedisTemplate.opsForZSet().removeRangeByScore(SecurityConst.REDIS_JTI_BLACKLIST, 0, now - 1);
        log.info("jti黑名单已刷新，条目数：{}，版本号：{}", this.blacklist.size(), this.version);
    }

    /**
     * 版本号自增
     *
     * @return 自增后的版本号
     */
    private long incrVersion() {
        Long v = this.stringRedisTemplate.opsForValue().increment(SecurityConst.REDIS_JTI_BLACKLIST_VERSION);
        return v == null ? System.currentTimeMillis() : v;
    }

    /**
     * 读取redis版本号，key不存在时为0
     *
     * @return 版本号
     */
    private long readVersion() {
        String v = this.stringRedisTemplate.opsForValue().get(SecurityConst.REDIS_JTI_BLACKLIST_VERSION);
        return this.parseVersion(v);
    }

    /**
     * 解析版本号，空或非数字时返回0
     *
     * @param value 版本号字符串
     * @return 版本号
     */
    private long parseVersion(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

}
