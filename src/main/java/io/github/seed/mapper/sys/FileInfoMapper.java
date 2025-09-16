package io.github.seed.mapper.sys;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryWrapper;
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
        return this.selectOneByQuery(QueryWrapper.create().eq(FileInfo::getFileId, fileId));
    }
}
