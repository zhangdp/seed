package io.github.seed.service.sys;

import io.github.seed.entity.sys.FileInfo;

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
     * 生成id
     *
     * @return
     */
    String generateId();
}
