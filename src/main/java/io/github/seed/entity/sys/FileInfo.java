package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseLogicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_FILE_INFO)
@Schema(description = "文件信息")
public class FileInfo extends BaseLogicEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识
     */
    @Schema(description = "文件id")
    private String fileId;
    /**
     * 上传者用户id
     */
    @Schema(description = "上传者用户id")
    private Long userId;
    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String fileName;
    /**
     * 文件后缀
     */
    @Schema(description = "文件后缀")
    private String extension;
    /**
     * 文件媒体类型
     */
    @Schema(description = "文件媒体类型")
    private String mimeType;
    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Long size;
    /**
     * 文件hash
     */
    @Schema(description = "文件hash")
    private String hash;
    /**
     * 保存路径
     */
    @Schema(description = "保存路径")
    private String storagePath;
    /**
     * 下载链接
     */
    @Schema(description = "下载链接")
    private String downloadUrl;
    /**
     * 上传时间
     */
    @Schema(description = "上传时间")
    private LocalDateTime uploadAt;
    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private LocalDateTime expireAt;

}
