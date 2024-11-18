package io.github.seed.common.config;

import io.github.seed.common.component.FileTemplate;
import io.github.seed.common.component.LocalFileTemplate;
import io.github.seed.common.component.MinioTemplate;
import io.github.seed.common.constant.MimeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 2024/11/8 文件访问器自动配置类
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
     * 本地文件访问器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "seed.file-storage", name = "type", havingValue = "local")
    public FileTemplate localFileTemplate() {
        FileTemplate template = new LocalFileTemplate(fileStorageProperties.getLocal().getUploadDir());
        log.info("使用本地文件访问器：{}，上传文件夹：{}", template, fileStorageProperties.getLocal().getUploadDir());
        return template;
    }

    /**
     * minio文件访问器
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "seed.file-storage", name = "type", havingValue = "minio")
    public FileTemplate minioFileTemplate() {
        FileStorageProperties.MinioProperties minioProperties = fileStorageProperties.getMinio();
        FileTemplate template = new MinioTemplate(minioProperties.getEndpoint(), minioProperties.getAccessKey(), minioProperties.getSecretKey(), minioProperties.getBucketName());
        log.info("使用MinIO文件访问器：{}, 地址：{}，桶：{}", template, minioProperties.getEndpoint(), minioProperties.getBucketName());
        return template;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> mimeTypes = fileStorageProperties.getMimeTypes();
        if (mimeTypes != null && !mimeTypes.isEmpty()) {
            mimeTypes.forEach((k, v) -> {
                if (v != null && !v.isEmpty()) {
                    String old = MimeType.addType(k, v);
                    log.info("新增自定义文件类型：文件后缀={}, 新类型={}, 原类型={}", k, v, old);
                }
            });
        }
    }
}
