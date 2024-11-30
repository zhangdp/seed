package io.github.seed.entity.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.LogicBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2024/11/8 文件信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(TableNameConst.SYS_FILE_INFO)
@Schema(description = "文件信息")
public class FileInfo extends LogicBaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件唯一标识
     */
    @Schema(title = "文件id")
    private String fileId;
    /**
     * 上传者用户id
     */
    @Schema(title = "上传者用户id")
    private Long userId;
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
     * 保存路径
     */
    @Schema(title = "保存路径")
    private String storagePath;
    /**
     * 下载链接
     */
    @Schema(title = "下载链接")
    private String downloadUrl;
    /**
     * 上传时间
     */
    @Schema(title = "上传时间")
    private LocalDateTime uploadTime;
    /**
     * 过期时间
     */
    @Schema(title = "过期时间")
    private LocalDateTime expireTime;

}
