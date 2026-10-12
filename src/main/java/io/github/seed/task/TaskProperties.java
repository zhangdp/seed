package io.github.seed.task;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 应用侧定时任务配置（{@code app.task}）
 * <br>这里只放代码里写死的定时任务的开关与间隔，改了要重启才生效；需要随时改频率的任务
 * 走任务表（{@code app.job}），由{@code sys_job.cron}驱动
 * <br>两个缓存刷新任务默认关闭：security模块的缓存本来就能在变更时广播并全量重拉，
 * 定时刷新只为兜住pub/sub丢通知，通知可靠时开了只是多一路无谓的读取
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@Component
@ConfigurationProperties(TaskProperties.CONFIG_PREFIX)
public class TaskProperties {

    public static final String CONFIG_PREFIX = "app.task";

    /**
     * jti黑名单定时全量刷新
     */
    private Refresh jtiBlacklistRefresh = new Refresh();
    /**
     * 角色权限缓存定时全量刷新
     */
    private Refresh rolePermissionRefresh = new Refresh();

    /**
     * 一个定时刷新任务的开关与间隔
     */
    @Getter
    @Setter
    public static class Refresh {

        /**
         * 是否启用定时刷新，为false时只靠变更通知同步
         */
        private boolean enabled = false;
        /**
         * 刷新间隔，上一轮结束后间隔该时间再发起下一轮
         */
        private Duration interval = Duration.ofMinutes(5);
    }
}
