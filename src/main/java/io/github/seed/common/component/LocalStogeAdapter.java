package io.github.seed.common.component;

import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.crypto.digest.DigestUtil;
import io.github.seed.common.data.StogeData;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.NotFoundException;
import io.github.seed.common.util.MimeType;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 本地文件访问适配器
 *
 * @author zhangdp
 * @since 2026/8/3
 */
@Getter
public class LocalStogeAdapter implements StogeAdapter {

    private final String rootPath;

    public LocalStogeAdapter(String rootPath) {
        this.rootPath = rootPath == null ? "" : rootPath.trim();
    }

    @SneakyThrows
    @Override
    public StogeData upload(String path, MultipartFile file) {
        File f = new File(this.normalizePath(path));
        FileUtil.mkParentDirs(f);
        file.transferTo(f);
        return this.toStogeData(f);
    }

    @Override
    public StogeData upload(String path, File file) {
        File targetFile = new File(this.normalizePath(path));
        FileUtil.copy(file, targetFile, true);
        return this.toStogeData(targetFile);
    }

    @Override
    public InputStream download(String path) {
        path = this.normalizePath(path);
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new NotFoundException(ErrorCode.FILE_NOT_FOUND.code(), "文件不存在：" + path, e);
        }
    }

    @Override
    public long download(String path, OutputStream out) {
        path = this.normalizePath(path);
        File file = new File(path);
        if (!file.exists()) {
            throw new NotFoundException(ErrorCode.FILE_NOT_FOUND.code(), "文件不存在：" + path);
        }
        return FileUtil.copy(file, out);
    }

    @Override
    public long download(String path, File file) {
        path = this.normalizePath(path);
        File f = new File(path);
        if (!f.exists()) {
            throw new NotFoundException(ErrorCode.FILE_NOT_FOUND.code(), "文件不存在：" + path);
        }
        FileUtil.copy(f, file, true);
        return file.length();
    }

    @Override
    public boolean delete(String path) {
        path = this.normalizePath(path);
        File file = new File(path);
        return file.delete();
    }

    @Override
    public List<String> deleteBatch(Collection<String> paths) {
        for (String path : paths) {
            path = this.normalizePath(path);
            this.delete(path);
        }
        return Collections.emptyList();
    }

    /**
     * 路径处理
     *
     * @param path
     * @return
     */
    public String normalizePath(String path) {
        if (path == null || (path = path.trim()).isEmpty()) {
            throw new IllegalArgumentException("路径不能为空");
        }
        // 组装完整路径
        if (!rootPath.isEmpty()) {
            path = rootPath + "/" + path;
        }
        // \替换为/
        path = path.replace('\\', '/');
        // 重复的/替换为单个/
        path = path.replaceAll("//+", "/");
        // 防止路径遍历攻击，如果包含 ../ 则抛出异常或进行过滤
        if (path.contains("../")) {
            throw new IllegalArgumentException("路径包含非法字符 ../，禁止路径遍历");
        }
        return path;
    }

    private StogeData toStogeData(File file) {
        StogeData data = new StogeData();
        data.setFileName(file.getName());
        data.setMimeType(MimeType.guessMimeType(data.getFileName()));
        String md5 = DigestUtil.md5Hex(file);
        data.setETag(md5);
        data.setChecksum(md5);
        data.setLastModified(file.lastModified());
        data.setExpire(null);
        data.setSize(file.length());
        data.setMetadata(null);
        return data;
    }

}
