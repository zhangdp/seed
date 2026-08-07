package io.github.seed.common.config;

import io.github.seed.common.component.S3StogeAdapter;
import io.github.seed.common.component.S3Template;
import io.github.seed.common.component.StogeAdapter;
import io.github.seed.common.component.LocalStogeAdapter;
import io.github.seed.common.util.MimeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 文件访问器自动配置类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
@RequiredArgsConstructor
public class FileStorageConfigurer implements InitializingBean {

    private final FileStorageProperties fileStorageProperties;

    /**
     * s3访问器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = FileStorageProperties.CONFIG_PREFIX + ".s3", name = "enabled", havingValue = "true")
    public S3Template s3Template() {
        FileStorageProperties.S3Properties s3Properties = fileStorageProperties.getS3();
        S3Template template = new S3Template(s3Properties.getEndpoint(), s3Properties.getAccessKey(), s3Properties.getSecretKey(), s3Properties.getBucket());
        log.info("创建S3访问器：{}", template);
        return template;
    }

    /**
     * aws-s3文件访问器
     *
     * @param s3Template
     * @return
     */
    @Bean
    @ConditionalOnBean(S3Template.class)
    public StogeAdapter s3StogeAdapter(S3Template s3Template) {
        S3StogeAdapter adapter = new S3StogeAdapter(s3Template);
        log.info("使用S3文件访问适配器：{}", adapter);
        return adapter;
    }

    /**
     * 本地文件访问器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(StogeAdapter.class)
    public StogeAdapter localStogeAdapter() {
        StogeAdapter template = new LocalStogeAdapter(fileStorageProperties.getLocal().getDir());
        log.warn("未发现文件访问器，使用本地文件访问器：{}", template);
        return template;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> mimeTypes = fileStorageProperties.getMimeTypes();
        if (mimeTypes != null && !mimeTypes.isEmpty()) {
            mimeTypes.forEach((k, v) -> {
                if (k != null && !(k = k.trim()).isEmpty() && v != null && !(v = v.trim()).isEmpty()) {
                    String old = MimeType.addType(k, v);
                    log.info("新增自定义文件类型：文件后缀={}, 新类型={}, 原类型={}", k, v, old);
                }
            });
        }
    }
}
