package io.github.seed.module.job;

import io.github.seed.module.job.component.JobCronSupport;
import io.github.seed.module.job.component.JobDispatcher;
import io.github.seed.module.job.component.JobInvoker;
import io.github.seed.module.job.store.JobStore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import tools.jackson.databind.json.JsonMapper;

/**
 * 定时任务模块装配
 * <br>只提供「挑任务、执行任务、推算触发时间」的能力，任务数据的读写与流程编排在{@code manager}
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(JobProperties.class)
public class JobConfigurer {

    /**
     * 任务调用器
     *
     * @param applicationContext 用于按名字取目标bean
     * @param jsonMapper         用于把json参数转成方法声明的类型
     * @return 任务调用器
     */
    @Bean
    public JobInvoker jobInvoker(ApplicationContext applicationContext, JsonMapper jsonMapper) {
        return new JobInvoker(applicationContext, jsonMapper);
    }

    /**
     * cron解析
     *
     * @return cron解析支持
     */
    @Bean
    public JobCronSupport jobCronSupport() {
        return new JobCronSupport();
    }

    /**
     * 调度引擎，数据读写全靠宿主注入的{@link JobStore}，模块本身不认识{@code service}
     *
     * @param jobStore       宿主提供的任务存储实现
     * @param jobInvoker     任务调用器
     * @param jobCronSupport cron解析
     * @param jobProperties  调度相关配置
     * @param environment    取本服务端口，心跳里记录节点地址
     * @return 调度引擎
     */
    @Bean
    public JobDispatcher jobDispatcher(JobStore jobStore, JobInvoker jobInvoker, JobCronSupport jobCronSupport,
                                       JobProperties jobProperties, Environment environment) {
        return new JobDispatcher(jobStore, jobInvoker, jobCronSupport, jobProperties, environment);
    }
}
