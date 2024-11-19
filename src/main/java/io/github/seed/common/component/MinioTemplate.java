package io.github.seed.common.component;

import io.github.seed.common.constant.Const;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.exception.NotFoundException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.beans.factory.InitializingBean;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
        this.client.setTimeout(Const.CONNECT_TIMEOUT, Const.READ_TIMEOUT * 4, Const.READ_TIMEOUT * 4);
        log.info("创建MinioClient: {}, endpoint={}, bucket={}", this.client, this.endpoint, this.bucket);
    }

    @Override
    public boolean isExists(String path) {
        return this.getInfo(path) != null;
    }

    @Override
    @SneakyThrows
    public boolean isEmptyDirectory(String path) {
        // 列出子文件时路径不能以/开头，且必须以/结尾
        String dir = StrUtil.stripAll(path, "/") + "/";
        try {
            Iterable<Result<Item>> results = this.client.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(this.bucket)
                            .prefix(dir)
                            .recursive(false)
                            .build()
            );
            // 检查是否有对象存在
            for (Result<Item> result : results) {
                Item item = result.get();
                // 排除自己还有则非空
                if (!item.isDir() || !item.objectName().equals(dir)) {
                    return false;
                }
            }
        } catch (Exception e) {
            this.resolveException("判断 " + path + " 是否是非空文件夹", e);
            return true;
        }
        return true;
    }

    @Override
    public boolean isDirectory(String path) {
        // minio没有直接的api判断是否文件夹，因此只能列出自己的方式
        // 不能以/开头不然结果会为空，不能以/结尾不然列出的是子文件而不是自己
        String prefix = StrUtil.stripAll(path, "/");
        try {
            Iterable<Result<Item>> results = this.client.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(this.bucket)
                            .prefix(prefix)
                            .recursive(false)
                            .build()
            );
            // 检查是否有对象存在
            String dir = prefix + "/";
            for (Result<Item> result : results) {
                Item item = result.get();
                if (item.isDir() && item.objectName().equals(dir)) {
                    return true;
                }
            }
        } catch (Exception e) {
            this.resolveException("判断 " + path + " 是否是文件夹", e);
            return false;
        }
        return false;
    }

    @Override
    public boolean mkdir(String path) {
        throw new UnsupportedOperationException("MinIO不支持创建文件夹");
        /*
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        String content = "";
        try (InputStream in = new ByteArrayInputStream(content.getBytes())) {
            return this.client.putObject(PutObjectArgs.builder().bucket(this.bucket).object(path).stream(in, in.available(), -1).build()) != null;
        } catch (Exception e) {
            this.resolveException("创建文件夹 " + path, e);
            return false;
        }
        */
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
        try {
            this.client.removeObject(RemoveObjectArgs.builder().bucket(this.bucket).object(path).build());
            return true;
        } catch (Exception e) {
            this.resolveException("删除文件 " + path, e);
            return false;
        }
    }

    @Override
    public boolean delete(String path, boolean isRecursive) {
        try {
            List<DeleteObject> list = new ArrayList<>();
            String dir = StrUtil.stripAll(path, "/") + "/";
            Iterable<Result<Item>> it = this.client.listObjects(ListObjectsArgs.builder().bucket(this.bucket).prefix(dir).recursive(true).build());
            for (Result<Item> itemResult : it) {
                list.add(new DeleteObject(itemResult.get().objectName()));
            }

            if (CollUtil.isNotEmpty(list)) {
                if (!isRecursive) {
                    throw new IllegalArgumentException("文件夹 " + path + " 不为空，无法删除");
                }
                Iterable<Result<DeleteError>> res = this.client.removeObjects(RemoveObjectsArgs.builder().bucket(this.bucket).objects(list).build());
                for (Result<DeleteError> re : res) {
                    DeleteError de = re.get();
                    System.out.println(de.objectName() + ":" + de.message());
                }
            }

            this.client.removeObject(RemoveObjectArgs.builder().bucket(this.bucket).object(path).build());
            return true;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            this.resolveException("删除文件 " + path, e);
            return false;
        }
    }

    @Override
    public boolean upload(File file, String path) {
        try {
            return this.upload(new FileInputStream(file), path);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("不存在文件 " + file.getAbsolutePath(), e);
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
            throw new IllegalArgumentException("不存在文件 " + file.getAbsolutePath(), e);
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
        log.error(operation, e);
        if (e instanceof ErrorResponseException ex) {
            String code = ex.errorResponse().code();
            if ("NoSuchKey".equals(code)) {
                throw new NotFoundException(ErrorCode.MINIO_NOT_FOUND_ERROR, e);
            }
        }
        throw new BizException(ErrorCode.MINIO_ERROR, e);
    }

}
