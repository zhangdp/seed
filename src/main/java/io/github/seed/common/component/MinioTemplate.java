package io.github.seed.common.component;

import io.github.seed.common.constant.Const;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.exception.NotFoundException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.io.IoUtil;
import org.springframework.beans.factory.InitializingBean;

import java.io.*;

/**
 * 2024/11/14 minio访问器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
@Slf4j
public class MinioTemplate implements FileTemplate, InitializingBean {

    @NotNull
    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucket;

    private MinioClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.client = MinioClient.builder().endpoint(this.endpoint).credentials(this.accessKey, this.secretKey).build();
        this.client.setTimeout(Const.CONNECT_TIMEOUT, Const.READ_TIMEOUT * 3, Const.READ_TIMEOUT * 3);
        log.info("创建MinioClient: {}, endpoint={}, bucket={}", this.client, this.endpoint, this.bucket);
    }

    @Override
    public boolean isExists(String path) {
        return this.getInfo(path) != null;
    }

    @Override
    public boolean isEmptyDirectory(String path) {
        // todo
        return false;
    }

    @Override
    public boolean isDirectory(String path) {
        // todo
        return false;
    }

    @Override
    public boolean mkdir(String path) {
        // minio没有单独创建文件夹的api，路径后面是/就是创建文件夹
        if (!path.endsWith("/")) {
            path += "/";
        }
        try {
            return this.client.putObject(PutObjectArgs.builder().bucket(this.bucket).object(path).build()) != null;
        } catch (Exception e) {
            this.resolveException("创建文件夹 " + path, e);
            return false;
        }
    }

    @Override
    public boolean copy(String oldPath, String newPath) {
        try {
            return this.client.copyObject(CopyObjectArgs.builder().bucket(this.bucket).object(newPath)
                    .source(CopySource.builder().bucket(this.bucket).object(oldPath).build())
                    .build()) != null;
        } catch (Exception e) {
            this.resolveException("复制文件" + oldPath + "到" + newPath, e);
            return false;
        }
    }

    @Override
    public boolean move(String oldPath, String newPath) {
        // minio没有移动的，因此先复制，然后删除原文件
        if (this.copy(oldPath, newPath)) {
            return this.delete(oldPath);
        }
        return false;
    }

    @Override
    public boolean delete(String path) {
        return this.delete(path, true);
    }

    @Override
    public boolean delete(String path, boolean isRecursive) {
        if (!isRecursive && this.isEmptyDirectory(path)) {
            throw new IllegalArgumentException("文件夹 ' " + path + " ' 不为空，无法删除");
        }
        try {
            this.client.removeObject(RemoveObjectArgs.builder().bucket(this.bucket).object(path).build());
            return true;
        } catch (Exception e) {
            this.resolveException("删除 " + path, e);
            return false;
        }
    }

    @Override
    public boolean upload(File file, String path) {
        try {
            return this.upload(new FileInputStream(file), path);
        } catch (FileNotFoundException e) {
            this.resolveException("上传文件到 " + path, e);
            return false;
        }
    }

    @Override
    public boolean upload(String localPath, String path) {
        return this.upload(new File(localPath), path);
    }

    @Override
    public boolean upload(byte[] bytes, String path) {
        return this.upload(new ByteArrayInputStream(bytes), path);
    }

    @Override
    public boolean upload(InputStream inputStream, String path) {
        try {
            return this.client.putObject(PutObjectArgs.builder()
                    .bucket(this.bucket).object(path).stream(inputStream, inputStream.available(), -1).build()) != null;
        } catch (Exception e) {
            this.resolveException("上传文件到 " + path, e);
            return false;
        }
    }

    @Override
    public InputStream download(String path) {
        InputStream in = null;
        try {
            in = this.client.getObject(GetObjectArgs.builder().bucket(this.bucket).object(path).build());
            return in;
        } catch (Exception e) {
            IoUtil.closeQuietly(in);
            this.resolveException("下载文件 " + path, e);
            return null;
        }
    }

    @Override
    public InputStream download(String path, long offset, long length) {
        InputStream in = null;
        try {
            in = this.client.getObject(GetObjectArgs.builder().bucket(this.bucket).object(path).offset(offset).length(length).build());
            return in;
        } catch (Exception e) {
            IoUtil.closeQuietly(in);
            this.resolveException("下载文件 " + path, e);
            return null;
        }
    }

    @Override
    public long download(String path, OutputStream outputStream) {
        InputStream inputStream = this.download(path);
        try {
            return IoUtil.copy(inputStream, outputStream);
        } catch (Exception e) {
            this.resolveException("下载文件 " + path, e);
            return -1;
        } finally {
            IoUtil.closeQuietly(outputStream, inputStream);
        }
    }

    @Override
    public long download(String path, long offset, long length, OutputStream outputStream) {
        InputStream inputStream = this.download(path, offset, length);
        try {
            return IoUtil.copy(inputStream, outputStream);
        } catch (Exception e) {
            this.resolveException("下载文件 " + path, e);
            return -1;
        } finally {
            IoUtil.closeQuietly(outputStream, inputStream);
        }
    }

    @Override
    public long download(String path, File file) {
        try {
            return this.download(path, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            this.resolveException("下载文件 " + path, e);
            return -1;
        }
    }

    @Override
    public long download(String path, String localPath) {
        return this.download(path, new File(localPath));
    }

    /**
     * 获取文件信息
     *
     * @param path
     * @return
     */
    private StatObjectResponse getInfo(String path) {
        try {
            return this.client.statObject(StatObjectArgs.builder().bucket(this.bucket).object(path).build());
        } catch (Exception e) {
            this.resolveException("获取文件 " + path + " 详情", e);
            return null;
        }
    }

    /**
     * 解析minio产生的异常
     *
     * @param operation
     * @param e
     */
    private void resolveException(String operation, Exception e) {
        if (e instanceof ErrorResponseException ex) {
            String code = ex.errorResponse().code();
            if ("NoSuchKey".equals(code) || "NoSuchBucket".equals(code) || "NoSuchVersion".equals(code)) {
                throw new NotFoundException(ErrorCode.MINIO_NOT_FOUND_ERROR.code(), operation + "失败：服务器不存在此文件", e);
            } else {
                throw new BizException(ErrorCode.MINIO_ERROR.code(), operation + "失败：请稍后再试", e);
            }
        } else if (e instanceof FileNotFoundException) {
            throw new BizException(ErrorCode.MINIO_ERROR.code(), operation + "失败：要上传的文件为空", e);
        } else {
            throw new BizException(ErrorCode.MINIO_ERROR.code(), operation + "失败，请稍后再试", e);
        }
    }

}
