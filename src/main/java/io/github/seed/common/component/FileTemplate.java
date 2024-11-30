package io.github.seed.common.component;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 2024/11/8 文件访问器
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface FileTemplate {

    /**
     * 文件/文件夹是否存在
     *
     * @param path
     * @return
     */
    boolean isExists(String path);

    /**
     * 是否是非空文件夹
     *
     * @param path
     * @return
     */
    boolean isEmptyDirectory(String path);

    /**
     * 是否是文件夹
     *
     * @param path
     * @return
     */
    boolean isDirectory(String path);

    /**
     * 创建文件夹，自动逐级创建不存在的父文件夹
     *
     * @param path
     * @return
     */
    boolean mkdir(String path);

    /**
     * 复制文件，默认覆盖
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    boolean copy(String oldPath, String newPath);

    /**
     * 移动文件，也可用于重命名，默认覆盖
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    boolean move(String oldPath, String newPath);

    /**
     * 删除文件/文件夹，默认不递归删除即用于删除文件或者空文件夹
     *
     * @param path
     * @return
     */
    boolean delete(String path);

    /**
     * 删除文件/文件夹
     *
     * @param path
     * @param isRecursive
     * @return
     */
    boolean delete(String path, boolean isRecursive);

    /**
     * 上传文件到指定路径，默认覆盖
     *
     * @param file
     * @param path
     * @return
     */
    boolean upload(File file, String path);

    /**
     * 上传本地路径文件到指定路径，默认覆盖
     *
     * @param localPath
     * @param path
     * @return
     */
    boolean upload(String localPath, String path);

    /**
     * 从内存中上传文件到指定路径，默认覆盖
     *
     * @param bytes
     * @param path
     * @return
     */
    boolean upload(byte[] bytes, String path);

    /**
     * 从输入流上传文件到指定路径，默认覆盖。会自动关闭输入流
     *
     * @param inputStream
     * @param path
     * @return
     */
    boolean upload(InputStream inputStream, String path);

    /**
     * 下载成输入流，用完流记得手动关闭
     *
     * @param path
     * @return
     */
    InputStream download(String path);

    /**
     * 分段下载
     *
     * @param path
     * @param offset
     * @param length
     * @return
     */
    InputStream download(String path, long offset, long length);

    /**
     * 下载到输出流
     *
     * @param path
     * @param outputStream
     * @return
     */
    long download(String path, OutputStream outputStream);

    /**
     * 分段下载到输出流
     *
     * @param path
     * @param offset
     * @param length
     * @param outputStream
     * @return
     */
    long download(String path, long offset, long length, OutputStream outputStream);

    /**
     * 下载到本地文件，默认覆盖
     *
     * @param path
     * @param file
     * @return
     */
    long download(String path, File file);

    /**
     * 下载到本地文件，默认覆盖
     *
     * @param path
     * @param localPath
     * @return
     */
    long download(String path, String localPath);

}
