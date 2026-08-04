package io.github.seed.common.component;

import cn.hutool.v7.core.io.file.FileUtil;
import cn.hutool.v7.crypto.digest.DigestUtil;
import io.github.seed.common.data.StogeData;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.exception.NotFoundException;
import io.github.seed.common.util.MimeType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author zhangdp
 * @since 2026/8/3
 */
@Getter
@Setter
public class LocalStogeAdapter implements StogeAdapter {

    private final String rootPath;

    public LocalStogeAdapter(String rootPath) {
        this.rootPath = rootPath == null ? "" : rootPath.trim();
    }

    @Override
    public StogeData upload(String path, MultipartFile file) {
        try {
            File f = new File(this.normalizePath(path));
            FileUtil.mkParentDirs(f);
            file.transferTo(f);
            return this.toStogeData(f);
        } catch (Exception e) {
            throw new BizException(ErrorCode.SERVER_ERROR.code(), "保存上传文件到本地磁盘失败", e);
        }
    }

    @Override
    public StogeData upload(String path, File file) {
        try {
            File targetFile = new File(this.normalizePath(path));
            FileUtil.copy(file, targetFile, true);
            return this.toStogeData(targetFile);
        } catch (Exception e) {
            throw new BizException(ErrorCode.SERVER_ERROR.code(), "保存本地文件失败", e);
        }
    }

    @Override
    public InputStream download(String path) {
        path = this.normalizePath(path);
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new NotFoundException(ErrorCode.NOT_FOUND.code(), "不存在文件" + path, e);
        }
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
