package io.github.seed.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2024/11/8 附件信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "附件信息")
public class FileInfoDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件id
     */
    @Schema(title = "文件id")
    private Long fileId;
    /**
     * 文件名
     */
    @Schema(title = "文件名")
    private String fileName;
    /**
     * 文件后缀
     */
    @Schema(title = "文件后缀")
    private String extension;
    /**
     * 文件媒体类型
     */
    @Schema(title = "文件媒体类型")
    private String mimeType;
    /**
     * 文件大小
     */
    @Schema(title = "文件大小")
    private Long fileSize;
    /**
     * 文件hash
     */
    @Schema(title = "文件hash")
    private String fileHash;
    /**
     * 下载链接
     */
    @Schema(title = "下载链接")
    private String downloadUrl;
    /**
     * 过期时间
     */
    @Schema(title = "过期时间", description = "9999年的则为永久文件永不过期")
    private LocalDateTime expireTime;

}
