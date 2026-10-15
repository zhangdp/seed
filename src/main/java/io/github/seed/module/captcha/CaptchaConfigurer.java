package io.github.seed.module.captcha;

import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.ImageCaptchaProperties;
import cloud.tianai.captcha.application.TACBuilder;
import cloud.tianai.captcha.cache.CacheStore;
import cloud.tianai.captcha.common.constant.CommonConstant;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import cloud.tianai.captcha.validator.impl.SimpleImageCaptchaValidator;
import io.github.seed.module.captcha.aspect.CaptchaVerifyAspect;
import io.github.seed.module.captcha.component.CaptchaGenerator;
import io.github.seed.module.captcha.data.CaptchaConst;
import io.github.seed.module.captcha.store.CaptchaStore;
import io.github.seed.module.captcha.store.RedisCaptchaStore;
import io.github.seed.module.captcha.store.TianaiRedisCacheStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 验证码模块装配：本模块的bean都在这里注册，读一个类就能看清装了什么
 * <br>对外只暴露{@link CaptchaService}；行为验证码基于tianai-captcha，背景图在
 * {@code classpath:captcha/image/bg/}，滑块/旋转模板与点选字体用tianai内置资源
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
@RequiredArgsConstructor
public class CaptchaConfigurer implements InitializingBean {

    /**
     * 行为验证码背景图的classpath目录，jpg格式、600x360，替换图片只需增删此目录下的文件
     */
    private static final String BACKGROUND_IMAGE_DIR = "captcha/image/bg/";

    private final CaptchaProperties captchaProperties;

    /**
     * 验证码生成器：随机码与key
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
     * tianai行为验证码的校验数据缓存（滑块位置、点选坐标），redis实现可替换官方的：
     * 官方starter基于SB 2.2.7编译，在SB4下运行会NoSuchMethodError
     *
     * @param stringRedisTemplate 校验数据存redis，多节点共享
     * @return tianai缓存
     */
    @Bean
    @ConditionalOnMissingBean(CacheStore.class)
    public CacheStore tianaiRedisCacheStore(StringRedisTemplate stringRedisTemplate) {
        return new TianaiRedisCacheStore(stringRedisTemplate);
    }

    /**
     * tianai行为验证码应用：生成器（四种类型）、校验器、图片转换都用tianai默认实现，
     * 这里只负责注册背景图资源与有效期
     *
     * @param cacheStore 校验数据缓存
     * @return tianai验证码应用
     * @throws IOException 背景图扫描失败
     */
    @Bean(destroyMethod = "close")
    public ImageCaptchaApplication imageCaptchaApplication(CacheStore cacheStore) throws IOException {
        LocalMemoryResourceStore resourceStore = new LocalMemoryResourceStore();
        this.registerBackgroundImages(resourceStore);
        ImageCaptchaProperties tianaiProps = this.buildTianaiProperties();
        return TACBuilder.builder()
                .setResourceStore(resourceStore)
                .setCacheStore(cacheStore)
                .setProp(tianaiProps)
                // 内置的滑块x2、旋转x1模板与点选字体
                .addDefaultTemplate()
                .build();
    }

    /**
     * 验证码服务
     *
     * @param captchaProperties       验证码配置
     * @param captchaStore            验证码读写（短信码与一次性票据）
     * @param captchaGenerator        随机码与key生成
     * @param imageCaptchaApplication tianai行为验证码应用
     * @return 验证码服务
     */
    @Bean
    public CaptchaService captchaService(CaptchaProperties captchaProperties, CaptchaStore captchaStore,
                                         CaptchaGenerator captchaGenerator,
                                         ImageCaptchaApplication imageCaptchaApplication) {
        return new CaptchaService(captchaProperties, captchaStore, captchaGenerator, imageCaptchaApplication);
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

    /**
     * 把背景图注册到每个启用的验证码类型下；增删图片只需增删{@code captcha/image/bg/}下的jpg
     *
     * @param resourceStore tianai资源存储
     * @throws IOException classpath扫描失败
     */
    private void registerBackgroundImages(LocalMemoryResourceStore resourceStore) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] images =
                resolver.getResources("classpath:" + BACKGROUND_IMAGE_DIR + "*.jpg");
        if (images.length == 0) {
            log.warn("未找到验证码背景图：classpath:{}，行为验证码将无法生成", BACKGROUND_IMAGE_DIR);
            return;
        }
        Set<String> types = captchaProperties.getImage().getTypes();
        for (String type : types) {
            for (org.springframework.core.io.Resource image : images) {
                resourceStore.addResource(type, new Resource(ClassPathResourceProvider.NAME,
                        BACKGROUND_IMAGE_DIR + image.getFilename(), CommonConstant.DEFAULT_TAG));
            }
        }
        log.info("注册验证码背景图{}张，覆盖类型{}", images.length, types);
    }

    /**
     * 把本模块配置翻译成tianai的配置：前缀沿用本模块redis key风格，各类型有效期统一取{@code image.expire}
     *
     * @return tianai配置
     */
    private ImageCaptchaProperties buildTianaiProperties() {
        ImageCaptchaProperties tianaiProps = new ImageCaptchaProperties();
        tianaiProps.setPrefix(CaptchaConst.REDIS_IMAGE_PREFIX);
        long expireMillis = captchaProperties.getImage().getExpire().toMillis();
        Map<String, Long> expire = new HashMap<>(8);
        expire.put("default", expireMillis);
        for (String type : captchaProperties.getImage().getTypes()) {
            expire.put(type, expireMillis);
        }
        tianaiProps.setExpire(expire);
        // 本地缓存生成结果属于吞吐优化，先关掉保持行为简单，需要时再开
        tianaiProps.setLocalCacheEnabled(false);
        return tianaiProps;
    }

    @Override
    public void afterPropertiesSet() {
        Set<String> scenes = captchaProperties.getSms().getScenes();
        if (scenes == null || scenes.isEmpty()) {
            log.warn("未配置{}.sms.scenes，短信场景不做白名单限制，存在轮换场景绕过发送频率限制的风险",
                    CaptchaProperties.CONFIG_PREFIX);
        } else {
            log.info("验证码类型：{}，行为验证码有效期：{}，短信场景白名单：{}，短信验证码有效期：{}",
                    captchaProperties.getImage().getTypes(), captchaProperties.getImage().getExpire(),
                    scenes, captchaProperties.getSms().getExpire());
        }
    }
}
