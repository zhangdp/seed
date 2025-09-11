package io.github.seed.mapper.sys;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.seed.entity.sys.FileInfo;
import io.github.seed.mapper.LambdaWrappersHelper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 2024/11/8 文件信息mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface FileInfoMapper extends BaseMapper<FileInfo>, LambdaWrappersHelper<FileInfo> {

    /**
     * 根据文件id查询
     *
     * @param fileId
     * @return
     */
    default FileInfo selectByFileId(String fileId) {
        return this.selectOne(lambdaQueryWrapper().eq(FileInfo::getFileId, fileId));
    }
}
