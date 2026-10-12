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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * jti黑名单：已登出、被踢下线的令牌在剩余有效期内仍然自带合法签名，只能靠拉黑挡住
 * <br>判定走节点内存，不查redis；redis只当持久化与跨节点同步的介质：
 * zset存「jti -> 拉黑到期时间戳」，条目到期即等于自动解除，无需额外删除
 * <br>同步只有「通知 + 全量重拉」一条路，不做版本号比对也没有定时兜底：pub/sub不持久化，
 * 断线期间的通知必然丢失，但重拉是全量的，下一次任何节点拉黑都会顺带把漏掉的补齐；
 * 要立刻对齐就调{@link #refresh()}（将来的管理后台"刷新缓存"就是调它）
 * <br>内存用不可变map整体替换：先clear再putAll会留下一段"谁都没被拉黑"的窗口，刷新越频繁越容易踩到
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class JtiBlacklist implements InitializingBean {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer listenerContainer;

    /**
     * 内存黑名单：jti -> 拉黑到期时间戳（毫秒）
     */
    private volatile Map<String, Long> blacklist = Map.of();

    @Override
    public void afterPropertiesSet() {
        // 先挂监听再预热：反过来的话，预热与挂监听之间那几毫秒发出的通知收不到，本节点会漏掉
        MessageListenerAdapter adapter = new MessageListenerAdapter(this, "onChanged");
        // 广播的是纯字符串，序列化方式必须和发送端（StringRedisTemplate）一致
        adapter.setSerializer(RedisSerializer.string());
        adapter.afterPropertiesSet();
        this.listenerContainer.addMessageListener(adapter, new ChannelTopic(SecurityConst.REDIS_JTI_BLACKLIST_CHANNEL));
        this.refresh();
        log.info("jti黑名单已预热，条目数：{}", this.blacklist.size());
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
        Map<String, Long> merged = new HashMap<>(this.blacklist);
        merged.putAll(items);
        this.blacklist = Map.copyOf(merged);
        this.stringRedisTemplate.convertAndSend(SecurityConst.REDIS_JTI_BLACKLIST_CHANNEL, SecurityConst.REDIS_CHANGE_SIGNAL);
        if (log.isDebugEnabled()) {
            log.debug("拉黑jti，数量：{}", items.size());
        }
    }

    /**
     * 是否被拉黑；已过期的条目视为未拉黑
     *
     * @param jti 令牌唯一标识
     * @return true表示已拉黑，该令牌不可用
     */
    public boolean isBlacklisted(String jti) {
        Long expireAt = this.blacklist.get(jti);
        return expireAt != null && expireAt > System.currentTimeMillis();
    }

    /**
     * 收到变更通知：直接全量重拉，不做差异比对
     *
     * @param message 通知内容，不使用
     */
    public void onChanged(String message) {
        this.refresh();
    }

    /**
     * 全量重拉：只拉未过期的条目，已过期的留在redis里由下次刷新顺手清掉
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
        this.blacklist = Map.copyOf(fresh);
        // 顺手清掉redis里已过期的条目，避免zset无限增长
        this.stringRedisTemplate.opsForZSet().removeRangeByScore(SecurityConst.REDIS_JTI_BLACKLIST, 0, now - 1);
        log.info("jti黑名单已刷新，条目数：{}", fresh.size());
    }
}
