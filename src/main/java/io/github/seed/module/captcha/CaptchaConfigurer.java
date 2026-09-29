package io.github.seed.module.captcha;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * 验证码自动配置类
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

    @Override
    public void afterPropertiesSet() {
        Set<String> scenes = captchaProperties.getSmsScenes();
        if (scenes == null || scenes.isEmpty()) {
            log.warn("未配置{}.sms-scenes，短信场景不做白名单限制，存在轮换场景绕过发送频率限制的风险",
                    CaptchaProperties.CONFIG_PREFIX);
        } else {
            log.info("验证码短信场景白名单：{}，图片验证码有效期：{}，短信验证码有效期：{}，发送间隔：{}",
                    scenes, captchaProperties.getImageExpire(), captchaProperties.getSmsExpire(),
                    captchaProperties.getSmsSendInterval());
        }
    }
}
