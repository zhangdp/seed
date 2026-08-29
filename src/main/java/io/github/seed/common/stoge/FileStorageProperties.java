package io.github.seed.common.stoge;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;

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
@ConfigurationProperties(FileStorageProperties.CONFIG_PREFIX)
public class FileStorageProperties {

    public static final String CONFIG_PREFIX = "app.file-storage";

    /**
     * 根目录
     */
    private String rootPath = "";
    /**
     * 默认过期时间，<=0用不过期
     */
    private Duration expireDuration = Duration.ZERO;
    /**
     * 下载配置
     */
    private DownloadProperties download;
    /**
     * 自定义mimeType，后缀名-类型
     */
    private Map<String, String> mimeTypes = new LinkedHashMap<>();
    /**
     * 本地保存配置
     */
    private LocalStorageProperties local;
    /**
     * s3配置
     */
    private S3Properties s3;

    /**
     * 本地文件存储配置
     */
    @Getter
    @Setter
    public static class LocalStorageProperties {

        /**
         * 上传的目录
         */
        private String dir = "";
    }

    /**
     * aws s3配置
     */
    @Getter
    @Setter
    public static class S3Properties {

        /**
         * 是否启用
         */
        private boolean enabled = false;

        /**
         * 访问地址
         */
        private String endpoint;

        /**
         * 用户名
         */
        private String accessKey;

        /**
         * 密码
         */
        private String secretKey;

        /**
         * 桶名称
         */
        private String bucket;

        /**
         * 地域
         */
        private String region = Region.US_EAST_1.id();

        /**
         * DNS格式是否开启路径格式模式
         */
        private boolean pathStyleAccessEnabled = true;

        /**
         * 请求负载时是否开启分块编码
         */
        private boolean chunkedEncodingEnabled = true;
    }

    /**
     * 下载配置
     */
    @Getter
    @Setter
    public static class DownloadProperties {

        /**
         * 下载url模板
         */
        @NotBlank
        private String urlTemplate = "/file/download/{fileId}/{fileName}";
        /**
         * 是否启用http 缓存
         */
        private boolean httpCacheable = true;
        /**
         * http缓存存活时间
         */
        private int httpCacheMaxAge = 604800;
    }

}
