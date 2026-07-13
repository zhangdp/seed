package io.github.seed.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import software.amazon.awssdk.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件类型工具类
 *
 * @author zhangdp
 * @since 2026/7/13
 */
@Slf4j
public class MimeTypes {

    /**
     * 默认二进制文件流类型application/octet-stream
     */
    public static final String MIMETYPE_OCTET_STREAM = "application/octet-stream";
    /**
     * 扩展名-文件类型
     */
    private final static Map<String, String> extensionToMimetype = new HashMap<>(1024);

    static {
        loadMimeTypes();
    }

    private MimeTypes() {}

    /**
     * 根据后缀获取文件类型，如果不存在则返回默认MIMETYPE_OCTET_STREAM
     *
     * @param extension
     * @return
     */
    public static String getMimeType(String extension) {
        return extensionToMimetype.getOrDefault(extension.toLowerCase(), MIMETYPE_OCTET_STREAM);
    }

    /**
     * 根据路径猜测文件类型
     *
     * @param path
     * @return
     */
    public static String guessMimeType(Path path) {
        return guessMimeType(path.getFileName().toString());
    }

    /**
     * 根据文件猜测文件类型
     *
     * @param file
     * @return
     */
    public static String guessMimeType(File file) {
        return guessMimeType(file.toPath());
    }

    /**
     * 根据文件名猜测文件类型
     *
     * @param fileName
     * @return
     */
    public static String guessMimeType(String fileName) {
        int lastPeriodIndex = fileName.lastIndexOf('.');
        if (lastPeriodIndex > 0 && lastPeriodIndex + 1 < fileName.length()) {
            String ext = StringUtils.lowerCase(fileName.substring(lastPeriodIndex + 1));
            if (extensionToMimetype.containsKey(ext)) {
                return extensionToMimetype.get(ext);
            }
        }
        return MIMETYPE_OCTET_STREAM;
    }

    /**
     * 获取所有 MIME 类型映射（返回不可修改的副本）
     *
     * @return
     */
    public static Map<String, String> getAllMimeTypes() {
        return Map.copyOf(extensionToMimetype);
    }

    /**
     * 从mime.types文件初始化
     */
    private static void loadMimeTypes() {
        try {
            ClassPathResource resource = new ClassPathResource("mime.types");

            if (!resource.exists()) {
                log.warn("mime.types 文件不存在于 resources 目录下！");
                return;
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();

                    // 跳过空行和注释行
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }

                    String[] parts = line.split("\\s+");
                    if (parts.length < 2) {
                        continue;
                    }

                    String mimeType = parts[0];
                    for (int i = 1; i < parts.length; i++) {
                        String ext = parts[i].toLowerCase().trim();
                        if (!ext.isEmpty()) {
                            extensionToMimetype.put(ext, mimeType);
                        }
                    }
                }
            }

            log.info("初始化完成，共加载 {} 个 MIME 类型", extensionToMimetype.size());

        } catch (Exception e) {
            log.error("加载 mime.types 文件失败", e);
        }
    }

}
