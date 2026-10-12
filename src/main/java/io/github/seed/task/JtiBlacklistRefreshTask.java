package io.github.seed.task;

import io.github.seed.module.security.component.JtiBlacklist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * jti黑名单定时全量刷新
 * <br>兜底用：pub/sub不持久化，节点与redis断线期间发出的变更通知收不到，那个节点会漏掉拉黑；
 * 定时重拉能把漏掉的补齐。变更通知正常时刷新前后内容一致，只有debug日志
 * <br>默认关闭（{@code app.task.jti-blacklist-refresh.enabled}），只在redis抖动、跨机房这类
 * 通知容易丢的场景打开；通知可靠时开启只是多一路无谓的redis读取
 * <br>间隔按上一轮结束再计时（fixedDelay），重拉变慢也不会叠加成并发刷新
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = TaskProperties.CONFIG_PREFIX + ".jti-blacklist-refresh",
        name = "enabled", havingValue = "true")
@RequiredArgsConstructor
public class JtiBlacklistRefreshTask {

    private final JtiBlacklist jtiBlacklist;

    /**
     * 全量重拉黑名单
     */
    @Scheduled(initialDelayString = "${app.task.jti-blacklist-refresh.interval:PT5M}",
            fixedDelayString = "${app.task.jti-blacklist-refresh.interval:PT5M}")
    public void refresh() {
        try {
            jtiBlacklist.refresh();
        } catch (Exception e) {
            // 刷新失败不能让调度线程死掉，否则本节点从此不再刷新
            log.error("jti黑名单定时刷新失败，本轮跳过", e);
        }
    }
}
