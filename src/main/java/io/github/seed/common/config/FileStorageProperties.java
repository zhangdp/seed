package io.github.seed.common.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 2024/11/11 文件配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(value = "seed.file-storage")
@Slf4j
public class FileStorageProperties {

    /**
     * 根目录
     */
    private String rootPath = "";
    /**
     * 下载url
     */
    @NotBlank
    private String downloadUrl = "/file/download/{fileId}/{fileName}";
    /**
     * 默认过期时间，<=0用不过期
     */
    private Duration expireDuration = Duration.ZERO;
    /**
     * 是否启用http 缓存
     */
    private boolean httpCacheable = true;
    /**
     * http缓存存活时间
     */
    private int httpCacheMaxAge = 604800;
    /**
     * 自定义mimeType，后缀名-类型
     */
    private Map<String, String> mimeTypes = new LinkedHashMap<>();
    /**
     * 附件保存方式
     */
    @NotNull
    private StorageType type;
    /**
     * 本地保存配置
     */
    private LocalStorageProperties local;
    /**
     * minio配置
     */
    // private MinioProperties minio;
    /**
     * aws s3配置
     */
    // private AwsS3Properties awss3;

    /**
     * 附件保存方式枚举
     */
    public enum StorageType {
        /**
         * 本地磁盘
         */
        LOCAL,
        /**
         * SFTP
         */
        // SFTP,
        /**
         * Minio
         */
        // MINIO,
        /**
         * aws s3
         */
        // AWSS3
    }

    /**
     * 本地文件存储配置
     */
    @Getter
    @Setter
    public static class LocalStorageProperties {

        /**
         * 上传的目录
         */
        private String uploadDir;
    }

    /**
     * minio配置
     *//*
    @Getter
    @Setter
    public static class MinioProperties {

        *//**
         * 访问地址
         *//*
        private String endpoint;

        *//**
         * 用户名
         *//*
        private String accessKey;

        *//**
         * 密码
         *//*
        private String secretKey;

        *//**
         * 桶名称
         *//*
        private String bucketName;
    }*/

    /**
     * aws s3配置
     *//*
    @Getter
    @Setter
    public static class AwsS3Properties {

        *//**
         * 访问地址
         *//*
        private String endpoint;

        *//**
         * 用户名
         *//*
        private String accessKey;

        *//**
         * 密码
         *//*
        private String secretKey;

        *//**
         * 桶名称
         *//*
        private String bucketName;
    }*/

}
