package io.github.seed.service.sys;

import io.github.seed.entity.sys.FileInfo;
import org.dromara.hutool.core.data.id.IdUtil;

/**
 * 2024/11/8 文件信息service
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface FileInfoService {

    /**
     * 新增
     *
     * @param fileInfo
     * @return
     */
    boolean add(FileInfo fileInfo);

    /**
     * 根据id获取文件信息
     *
     * @param fileId
     * @return
     */
    FileInfo getByFileId(String fileId);

    /**
     * 生成文件id
     *
     * @return
     */
    default String generateId() {
        // 雪花id
        return IdUtil.getSnowflakeNextIdStr();
    }
}
