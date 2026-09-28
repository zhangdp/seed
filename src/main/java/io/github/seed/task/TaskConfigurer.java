package io.github.seed.task;

import io.github.seed.manager.SmsManager;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.service.sys.SmsLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务自动配置类
 * <br>调度方统一在此装配：定时任务属于应用层的调度策略，不随可插拔模块（{@code module}包）一起走，
 * 模块只提供能力、由这里的任务驱动
 * <br>{@code app.sms.send-enabled}为false时短信派发任务不注册（改为消息队列调度时用），
 * 发送入口{@link SmsManager#dispatch}不变
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
public class TaskConfigurer {

    /**
     * 内置的短信定时派发任务
     * <br>{@code app.sms.send-enabled}为false时本Bean不注册（改为消息队列调度时用），
     * 发送入口{@link SmsManager#dispatch}不变
     *
     * @param smsManager     短信管理器
     * @param smsLogService  短信日志服务
     * @param smsProperties  短信配置
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.CONFIG_PREFIX, name = "send-enabled",
            havingValue = "true", matchIfMissing = true)
    public SmsSendTask smsSendTask(SmsManager smsManager, SmsLogService smsLogService, SmsProperties smsProperties) {
        return new SmsSendTask(smsManager, smsLogService, smsProperties);
    }

}
