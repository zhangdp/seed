package io.github.seed.task;

import io.github.seed.manager.SmsManager;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.service.sys.SmsLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发短信定时任务装配
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
public class TaskConfigurer {

    /**
     * 短信定时派发任务，{@code app.sms.send-enabled}为false时不注册
     *
     * @param smsManager     短信管理器
     * @param smsLogService  短信日志service
     * @param smsProperties  短信配置
     * @return 短信定时派发任务
     */
    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.CONFIG_PREFIX, name = "send-enabled",
            havingValue = "true", matchIfMissing = true)
    public SmsSendTask smsSendTask(SmsManager smsManager, SmsLogService smsLogService, SmsProperties smsProperties) {
        return new SmsSendTask(smsManager, smsLogService, smsProperties);
    }

}
