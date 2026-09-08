package io.github.seed.common.stoge;

import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * 2024/11/8 存储适配器接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface StogeAdapter {

    /**
     * 上传输入流文件
     *
     * @param path
     * @param inputStream
     * @param size
     * @param fileName
     * @return
     */
    StogeData upload(String path, InputStream inputStream, long size, String fileName);

    /**
     * 上传从前端上传的文件
     *
     * @param path
     * @param file
     * @return
     */
    default StogeData upload(String path, MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            return this.upload(path, in, file.getSize(), file.getOriginalFilename());
        } catch (IOException e) {
            throw new BadRequestException(ErrorCode.REQUEST_BODY_NOT_READABLE.code(), file.getOriginalFilename() + "上传失败", e);
        }
    }

    /**
     * 上传本地文件
     *
     * @param path
     * @param file
     * @return
     */
    StogeData upload(String path, File file);

    /**
     * 上传本地文件
     *
     * @param path
     * @param localPath
     * @return
     */
    default StogeData upload(String path, String localPath) {
        return this.upload(path, new File(localPath));
    }

    /**
     * 下载成输入流，用完流记得关闭
     *
     * @param path
     * @return
     */
    InputStream download(String path);

    /**
     * 下载到输出流
     *
     * @param path
     * @param out
     * @return
     */
    long download(String path, OutputStream out);

    /**
     * 下载到本地文件
     *
     * @param path
     * @param file
     * @return
     */
    long download(String path, File file);

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    boolean delete(String path);

    /**
     * 批量删除，返回失败列表
     *
     * @param paths
     * @return
     */
    default List<String> deleteBatch(String... paths) {
        return this.deleteBatch(List.of(paths));
    }

    /**
     * 批量删除，返回失败列表
     *
     * @param paths
     * @return
     */
    List<String> deleteBatch(Collection<String> paths);

}
