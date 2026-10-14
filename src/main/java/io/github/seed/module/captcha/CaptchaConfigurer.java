package io.github.seed.module.captcha;

import io.github.seed.module.captcha.aspect.CaptchaVerifyAspect;
import io.github.seed.module.captcha.component.CaptchaGenerator;
import io.github.seed.module.captcha.store.CaptchaStore;
import io.github.seed.module.captcha.store.RedisCaptchaStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

/**
 * 验证码模块装配：本模块的bean都在这里注册，读一个类就能看清装了什么
 * <br>对外只暴露{@link CaptchaService}，生成与存储是内部实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
@RequiredArgsConstructor
public class CaptchaConfigurer implements InitializingBean {

    private final CaptchaProperties captchaProperties;

    /**
     * 验证码生成器
     *
     * @return 验证码生成器
     */
    @Bean
    public CaptchaGenerator captchaGenerator() {
        return new CaptchaGenerator();
    }

    /**
     * 验证码读写，默认存redis；宿主想换存储时自己注册一个{@link CaptchaStore}即可顶掉
     *
     * @param stringRedisTemplate 验证码存redis，多节点共享才不会出现"换台机器就校验失败"
     * @return 验证码读写
     */
    @Bean
    @ConditionalOnMissingBean(CaptchaStore.class)
    public CaptchaStore redisCaptchaStore(StringRedisTemplate stringRedisTemplate) {
        return new RedisCaptchaStore(stringRedisTemplate);
    }

    /**
     * 验证码服务
     *
     * @param captchaProperties 验证码配置
     * @param captchaStore      验证码读写
     * @param captchaGenerator  验证码生成器
     * @return 验证码服务
     */
    @Bean
    public CaptchaService captchaService(CaptchaProperties captchaProperties, CaptchaStore captchaStore,
                                         CaptchaGenerator captchaGenerator) {
        return new CaptchaService(captchaProperties, captchaStore, captchaGenerator);
    }

    /**
     * 验证码校验切面
     *
     * @param captchaService 验证码服务
     * @return 验证码校验切面
     */
    @Bean
    public CaptchaVerifyAspect captchaVerifyAspect(CaptchaService captchaService) {
        return new CaptchaVerifyAspect(captchaService);
    }

    @Override
    public void afterPropertiesSet() {
        Set<String> scenes = captchaProperties.getSms().getScenes();
        if (scenes == null || scenes.isEmpty()) {
            log.warn("未配置{}.sms.scenes，短信场景不做白名单限制，存在轮换场景绕过发送频率限制的风险",
                    CaptchaProperties.CONFIG_PREFIX);
        } else {
            log.info("验证码短信场景白名单：{}，图片验证码有效期：{}，短信验证码有效期：{}",
                    scenes, captchaProperties.getImage().getExpire(), captchaProperties.getSms().getExpire());
        }
    }
}
