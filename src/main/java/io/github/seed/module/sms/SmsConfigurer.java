package io.github.seed.module.sms;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.module.sms.sender.LogSmsSender;
import io.github.seed.module.sms.sender.SmsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信自动配置类
 * <br>本模块只提供<b>短信发送能力</b>：接口{@link SmsSender}、默认实现与配置，不感知数据库，
 * 短信记录的落库与结果回写由业务编排层{@code SmsManager}完成，调度由应用层的定时任务/消息队列驱动
 * <br>本模块的Bean统一在此装配，不散落在各实现类上：本模块的装配策略（谁默认生效、谁可被替换）
 * 集中在一处，改起来不用到处找{@code @Component}
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsConfigurer implements InitializingBean {

    private final SmsProperties smsProperties;

    public SmsConfigurer(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    /**
     * 默认的短信发送实现，业务方自定义{@link SmsSender}的Bean后会自动让位
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(SmsSender.class)
    public SmsSender smsSender() {
        log.warn("未发现自定义的短信发送实现，使用日志Mock实现LogSmsSender，不会真实发送短信");
        return new LogSmsSender(smsProperties);
    }

    @Override
    public void afterPropertiesSet() {
        if (StrUtil.isBlank(smsProperties.getSignName())) {
            log.warn("未配置{}.sign-name，接入短信服务商后会被服务商以签名不合法拒绝", SmsProperties.CONFIG_PREFIX);
        } else {
            log.info("短信签名：{}，已配置模板的场景：{}", smsProperties.getSignName(),
                    smsProperties.getTemplates().keySet());
        }
    }
}
