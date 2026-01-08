package io.github.seed.common.component;

import cn.hutool.v7.core.io.IoUtil;
import cn.hutool.v7.core.io.file.FileUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 本地文件访问器实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class LocalFileTemplate implements FileTemplate {

    /**
     * 根目录
     */
    @Getter
    private final String uploadDir;

    public LocalFileTemplate() {
        this.uploadDir = "";
    }

    public LocalFileTemplate(String uploadDir) {
        this.uploadDir = uploadDir == null ? "" : uploadDir.trim();
    }

    /**
     * 完整路径
     *
     * @param path
     * @return
     */
    public String fullPath(String path) {
        if (path == null || (path = path.trim()).isEmpty()) {
            throw new IllegalArgumentException("路径不能为空");
        }
        String fullPath = this.uploadDir;
        if (!this.uploadDir.isEmpty()) {
            fullPath += "/";
        }
        fullPath += path;
        return this.normalizePath(fullPath);
    }

    /**
     * 是否是空闲文件夹
     *
     * @param path
     * @return
     */
    public boolean isEmptyDirectory(String path) {
        return FileUtil.isDirEmpty(new File(this.fullPath(path)));
    }

    @Override
    public boolean isExists(String path) {
        return FileUtil.exists(this.fullPath(path));
    }

    @Override
    public boolean isDirectory(String path) {
        return FileUtil.isDirectory(this.fullPath(path));
    }

    @Override
    public boolean mkdir(String path) {
        return FileUtil.mkdir(this.fullPath(path)) != null;
    }

    @Override
    public boolean copy(String oldPath, String newPath) {
        return FileUtil.copy(this.fullPath(oldPath), this.fullPath(newPath), true) != null;
    }

    @Override
    public boolean move(String oldPath, String newPath) {
        return FileUtil.move(new File(this.fullPath(oldPath)), new File(this.fullPath(newPath)), true) != null;
    }

    @Override
    public boolean delete(String path) {
        return this.delete(path, false);
    }

    @Override
    public boolean delete(String path, boolean recursive) {
        String fullPath = this.fullPath(path);
        if (!recursive && !this.isEmptyDirectory(fullPath)) {
            throw new IllegalArgumentException("文件夹 '" + fullPath + "' 不为空，无法删除");
        }
        FileUtil.del(fullPath);
        return true;
    }

    @Override
    public boolean upload(File file, String path) {
        return FileUtil.copy(file, new File(this.fullPath(path)), true) != null;
    }

    @Override
    public boolean upload(String localPath, String path) {
        return FileUtil.copy(this.fullPath(localPath), this.fullPath(path), true) != null;
    }

    @Override
    public boolean upload(byte[] bytes, String path) {
        return FileUtil.writeBytes(bytes, this.fullPath(path)) != null;
    }

    @Override
    public boolean upload(InputStream inputStream, String path) {
        return FileUtil.writeFromStream(inputStream, new File(this.fullPath(path)), true) != null;
    }

    @Override
    public InputStream download(String path) {
        return FileUtil.getInputStream(this.fullPath(path));
    }

    @Override
    public InputStream download(String path, long offset, long length) {
        // todo 暂时没有好的算法
        throw new UnsupportedOperationException();
    }

    @Override
    public long download(String path, OutputStream outputStream) {
        return FileUtil.copy(new File(this.fullPath(path)), outputStream);
    }

    @Override
    public long download(String path, long offset, long length, OutputStream outputStream) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(this.fullPath(path), "r")) {
            raf.seek(offset);
            byte[] buffer = new byte[8192];
            int read;
            long total = 0L;
            while ((read = raf.read(buffer)) != -1) {
                long left = length - total;
                int len = read > left ? (int) left : read;
                outputStream.write(buffer, 0, len);
                total += len;
                if (total >= length) {
                    break;
                }
            }
            outputStream.flush();
            return total;
        } finally {
            IoUtil.closeQuietly(outputStream);
        }
    }

    @Override
    public long download(String path, File file) {
        return FileUtil.copy(new File(this.fullPath(path)), file, true).length();
    }

    @Override
    public long download(String path, String localPath) {
        return this.download(path, new File(localPath));
    }

}
