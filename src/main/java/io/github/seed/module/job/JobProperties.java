package io.github.seed.module.job;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 定时任务配置（{@code app.job}）
 * <br>集群相关的时间参数要互相匹配：{@code nodeTimeout}应大于{@code heartbeatInterval}的2~3倍，
 * 否则网络抖动一下节点就被判定下线；{@code fireTimeout}要大于单个任务的最长可能耗时
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(JobProperties.CONFIG_PREFIX)
public class JobProperties {

    public static final String CONFIG_PREFIX = "app.job";

    /**
     * 是否启用本节点的调度能力，为false时本节点不挑任务也不发心跳（纯业务节点）
     */
    private boolean enabled = true;
    /**
     * 挑任务的轮询间隔，上一轮结束后间隔该时间再发起下一轮
     */
    private Duration pollInterval = Duration.ofSeconds(3);
    /**
     * 每轮最多挑多少个任务，防止一次拉太多把本节点压满
     */
    private int fetchSize = 20;
    /**
     * 心跳间隔
     */
    private Duration heartbeatInterval = Duration.ofSeconds(15);
    /**
     * 节点心跳超过该时间没续期就视为已下线，其未跑完的任务由存活节点接管
     */
    private Duration nodeTimeout = Duration.ofSeconds(45);
    /**
     * 任务被抢占后超过该时间仍未回写视为卡死（执行线程被打断、进程被强杀），触发回收
     */
    private Duration fireTimeout = Duration.ofMinutes(30);
}
