package io.github.seed.service.sys.impl;

import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.sys.FileInfo;
import io.github.seed.mapper.sys.FileInfoMapper;
import io.github.seed.service.sys.FileInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 2024/11/8 文件信息service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = TableNameConst.SYS_FILE_INFO)
public class FileInfoServiceImpl implements FileInfoService {

    private final FileInfoMapper fileInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(FileInfo fileInfo) {
        if (fileInfo.getFileId() == null || fileInfo.getFileId().isEmpty()) {
            fileInfo.setFileId(this.generateId());
        }
        if (fileInfo.getUploadAt() == null) {
            fileInfo.setUploadAt(LocalDateTime.now());
        }
        if (fileInfo.getExpireAt() == null) {
            fileInfo.setExpireAt(Const.MAX_LOCAL_DATE_TIME);
        }
        return this.fileInfoMapper.insert(fileInfo) > 0;
    }

    @Override
    @Cacheable(key = "#fileId")
    public FileInfo getByFileId(String fileId) {
        return fileInfoMapper.selectByFileId(fileId);
    }
}
