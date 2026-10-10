package io.github.seed.module.job.component;

import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * cron表达式解析与下次触发时间推算
 * <br>用spring自带的{@link CronExpression}（6位：秒 分 时 日 月 周），不引quartz；
 * 解析结果按表达式缓存，避免每次触发都重新解析
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class JobCronSupport {

    private final Map<String, CronExpression> cache = new ConcurrentHashMap<>();

    /**
     * 推算下次触发时间，返回值一定晚于传入的时间点
     *
     * @param cron  cron表达式
     * @param after 推算起点
     * @return 下次触发时间
     */
    public LocalDateTime next(String cron, LocalDateTime after) {
        return this.parse(cron).next(after);
    }

    /**
     * 解析cron表达式，非法表达式抛{@code IllegalArgumentException}
     *
     * @param cron cron表达式
     * @return 解析结果
     */
    public CronExpression parse(String cron) {
        return cache.computeIfAbsent(cron, CronExpression::parse);
    }
}
