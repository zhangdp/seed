package io.github.seed.module.sms;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.module.sms.limiter.RedisSmsSendLimiter;
import io.github.seed.module.sms.limiter.SmsSendLimiter;
import io.github.seed.module.sms.sender.LogSmsSender;
import io.github.seed.module.sms.sender.SmsSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 短信模块装配
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
     * 默认发送器，业务方注册自定义{@link SmsSender}的Bean后自动让位
     *
     * @return 短信发送器
     */
    @Bean
    @ConditionalOnMissingBean(SmsSender.class)
    public SmsSender smsSender() {
        log.warn("未发现自定义的短信发送实现，使用日志Mock实现LogSmsSender，不会真实发送短信");
        return new LogSmsSender(smsProperties);
    }

    /**
     * 发送限流器，默认存redis；宿主想换存储（如改从sys_sms_log统计）时自己注册一个
     * {@link SmsSendLimiter}即可顶掉
     *
     * @param stringRedisTemplate 锁存redis，跨节点才拦得住换机器重发
     * @return 短信发送限流器
     */
    @Bean
    @ConditionalOnMissingBean(SmsSendLimiter.class)
    public SmsSendLimiter redisSmsSendLimiter(StringRedisTemplate stringRedisTemplate) {
        return new RedisSmsSendLimiter(stringRedisTemplate);
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
