package io.github.seed.task;

import io.github.seed.manager.SmsManager;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.service.sys.SmsLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 调度层装配：定时任务属于应用层的调度策略，不随可插拔模块走，模块只提供能力、由这里的任务驱动
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@EnableScheduling
public class TaskConfigurer {

    /**
     * 短信定时派发任务，{@code app.sms.send-enabled}为false时不注册（改为消息队列调度时用），
     * 发送入口{@link SmsManager#dispatch}不变
     */
    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.CONFIG_PREFIX, name = "send-enabled",
            havingValue = "true", matchIfMissing = true)
    public SmsSendTask smsSendTask(SmsManager smsManager, SmsLogService smsLogService, SmsProperties smsProperties) {
        return new SmsSendTask(smsManager, smsLogService, smsProperties);
    }

}
