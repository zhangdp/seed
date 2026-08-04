package io.github.seed.common.component;

import cn.hutool.v7.core.io.IoUtil;
import cn.hutool.v7.core.io.file.FileNameUtil;
import io.github.seed.common.data.StogeData;
import io.github.seed.common.exception.NotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
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
     * 上传从前端上传的文件
     *
     * @param path
     * @param file
     * @return
     */
    StogeData upload(String path, MultipartFile file);

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
    default long download(String path, OutputStream out) {
        InputStream in = null;
        try {
            in = this.download(path);
            if (in == null) {
                throw new NotFoundException("不存在文件" + FileNameUtil.getName(path));
            }
            return IoUtil.copy(in, out);
        } finally {
            IoUtil.closeQuietly(in);
        }
    }

    /**
     * 下载到本地文件
     *
     * @param path
     * @param file
     * @return
     */
    default long download(String path, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            return this.download(path, out);
        } catch (FileNotFoundException e) {
            throw new NotFoundException("不存在文件" + file.getName(), e);
        } finally {
            IoUtil.closeQuietly(out);
        }
    }

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
