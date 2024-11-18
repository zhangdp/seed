package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.seed.entity.sys.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2024/11/8 文件信息mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 根据文件id查询
     *
     * @param fileId
     * @return
     */
    default FileInfo selectByFileId(String fileId) {
        return this.selectOne(Wrappers.lambdaQuery(FileInfo.class).eq(FileInfo::getFileId, fileId));
    }
}
