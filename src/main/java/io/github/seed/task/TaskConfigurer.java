package io.github.seed.task;

import io.github.seed.manager.JobManager;
import io.github.seed.manager.SmsManager;
import io.github.seed.module.job.JobProperties;
import io.github.seed.module.sms.SmsProperties;
import io.github.seed.service.sys.SmsLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 任务装配
 * <br>这里只放「被调度的东西」：调度器本身、以及任务表里会通过{@code beanName.methodName}引用到的任务bean
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
public class TaskConfigurer {

    /**
     * 定时任务调度器，{@code app.job.enabled}为false时本节点不参与调度
     *
     * @param jobManager 任务编排
     * @return 调度任务
     */
    @Bean
    @ConditionalOnProperty(prefix = JobProperties.CONFIG_PREFIX, name = "enabled",
            havingValue = "true", matchIfMissing = true)
    public JobScheduleTask jobScheduleTask(JobManager jobManager) {
        return new JobScheduleTask(jobManager);
    }

    /**
     * 短信派发任务，任务表里的{@code smsDispatchJob}指向它的{@code dispatchPending}
     * <br>启停由任务表控制，不再靠配置项开关
     *
     * @param smsManager    短信管理器
     * @param smsLogService 短信日志service
     * @param smsProperties 短信配置
     * @return 短信派发任务
     */
    @Bean
    public SmsSendTask smsSendTask(SmsManager smsManager, SmsLogService smsLogService, SmsProperties smsProperties) {
        return new SmsSendTask(smsManager, smsLogService, smsProperties);
    }

    /**
     * 示例任务，任务表里的{@code heartbeatDemoJob}指向它的{@code tick}
     *
     * @return 示例任务
     */
    @Bean
    public DemoJob demoJob() {
        return new DemoJob();
    }

}
