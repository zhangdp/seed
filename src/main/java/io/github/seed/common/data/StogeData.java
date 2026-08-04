package io.github.seed.common.data;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 存储对象信息
 *
 * @author zhangdp
 * @since 2026/7/31
 */
@Data
public class StogeData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件类型
     */
    private String mimeType;
    /**
     * eTag
     */
    private String eTag;
    /**
     * 文件校验码
     */
    private String checksum;
    /**
     * 上次修改时间
     */
    private Long lastModified;
    /**
     * 过期时间
     */
    private Long expire;
    /**
     * 大小
     */
    private Long size;
    /**
     * 元数据
     */
    private Map<String, String> metadata;
}
