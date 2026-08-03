package io.github.seed.common.component;

import cn.hutool.v7.core.collection.CollUtil;
import cn.hutool.v7.core.io.IoUtil;
import io.github.seed.common.util.MimeType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;

import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * S3访问类，适用于任何S3协议的文件存储如minio、阿里oss、腾讯cos、电信翼龙ocs等
 *
 * <p>
 * S3 是对象存储，不是文件系统。
 * 对象以 key（字符串） 作为唯一标识，存储在扁平的命名空间中。
 * “目录”只是 key 的前缀约定，底层并没有真正的目录树结构。
 * 对象不可变（Immutable），一旦写入，对象内容 + key 就是固定的
 * </p>
 *
 * @author zhangdp
 * @since 2026/7/29
 */
@Getter
@RequiredArgsConstructor
@Slf4j
public class S3Template implements InitializingBean, DisposableBean {

    private final String endpoint;
    private final String accessKey;
    private final String secretKey;
    private final String bucket;

    private S3Client s3Client;

    // s3批量处理一次性最多1000条
    public static final int BATCH_SIZE = 1000;

    @Override
    public void afterPropertiesSet() {
        this.init();
    }

    @Override
    public void destroy() {
        if (s3Client != null) {
            try {
                s3Client.close();
                log.info("[{}]S3 Client {} 已关闭", bucket, s3Client);
            } catch (Exception e) {
                log.warn("[{}]关闭S3 Client {} 异常", bucket, s3Client, e);
            } finally {
                s3Client = null;
            }
        }
    }

    /**
     * 初始化
     */
    public void init() {
        Assert.hasText(endpoint, "endpoint不能为空");
        Assert.hasText(accessKey, "accessKey不能为空");
        Assert.hasText(secretKey, "secretKey不能为空");
        Assert.hasText(bucket, "bucket不能为空");
        s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                // 没用但必须填
                .region(Region.US_EAST_1)
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
        log.info("[{}]初始化S3 Client：{}", bucket, s3Client);
    }

    /**
     * 获取桶bucket元数据
     *
     * @return
     */
    public HeadBucketResponse headBucket() {
        try {
            return s3Client.headBucket(
                    HeadBucketRequest.builder()
                            .bucket(bucket)
                            .build()
            );
        } catch (NoSuchBucketException e) {
            return null;
        }
    }

    /**
     * 创建桶bucket，创建成功返回true，如果已存在返回false，失败会抛异常
     */
    public boolean createBucket() {
        try {
            CreateBucketResponse res = s3Client.createBucket(
                    CreateBucketRequest.builder()
                            .bucket(bucket)
                            .build()
            );
            log.info("[{}] Bucket创建成功，result={}", bucket, res);
            return true;
        } catch (BucketAlreadyOwnedByYouException e) {
            log.info("[{}] Bucket已存在，无需重复创建", bucket);
            return false;
        }
    }

    /**
     * 获取对象元数据
     *
     * @param path
     * @return
     */
    public HeadObjectResponse headObject(String path) {
        path = this.normalizePath(path);
        try {
            return s3Client.headObject(
                    HeadObjectRequest.builder()
                        .bucket(bucket)
                        .key(path)
                        .build()
            );
        } catch (NoSuchKeyException e) {
            log.warn("[{}]不存在文件：{}", bucket, path, e);
            return null;
        }
    }

    /**
     * 列出前缀下的对象（相当于列出子文件列表），默认最多列出1000个
     *
     * @param path
     * @path
     */
    public ListObjectsV2Response listObjects(String path) {
        return this.listObjects(path, null);
    }

    /**
     * 列出前缀下的对象列表（相当于列出子文件列表）。比如列出1个，可用于快速判断目录非空
     *
     * @param path
     * @param maxKeys
     * @return
     */
    public ListObjectsV2Response listObjects(String path, Integer maxKeys) {
        path = this.directoryStylePath(this.normalizePath(path));
        Assert.isTrue(maxKeys == null || maxKeys > 0 && maxKeys <= BATCH_SIZE, "maxKey必须大于0且小于等于" + BATCH_SIZE);
        return s3Client.listObjectsV2(
                ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(path)
                    .maxKeys(maxKeys)
                    .build()
        );
    }

    /**
     * 分页列出前缀下的对象（相当于列出子文件列表）
     *
     * @param path
     * @return
     */
    public ListObjectsV2Iterable listObjectsPaginator(String path) {
        return this.listObjectsPaginator(path, null);
    }

    /**
     * 分页列出前缀下的对象（相当于列出子文件列表）
     *
     * @param path
     * @param maxKeys
     * @return
     */
    public ListObjectsV2Iterable listObjectsPaginator(String path, Integer maxKeys) {
        path = this.directoryStylePath(this.normalizePath(path));
        Assert.isTrue(maxKeys == null || maxKeys > 0 && maxKeys <= BATCH_SIZE, "maxKey必须大于0且小于等于" + BATCH_SIZE);
        return s3Client.listObjectsV2Paginator(
                ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .prefix(path)
                        .maxKeys(maxKeys)
                        .build()
        );
    }

    /**
     * 是否存在，s3没有目录的概念，只能判断文件是否存在
     *
     * @param path
     * @return
     */
    public boolean isExists(String path) {
        return this.headObject(path) != null;
    }

    /**
     * 是否空目录，s3没有目录的概念，只能按照路径前缀去查询存在即认为非空目录
     *
     * @param path
     * @return
     */
    public boolean isEmptyDirectory(String path) {
        ListObjectsV2Response res = this.listObjects(path, 1);
        return res == null || !res.hasContents();
    }

    /**
     * 复制文件，s3没有目录的概念，不支持目录
     *
     * @param srcPath
     * @param destPath
     * @return
     */
    public CopyObjectResponse copy(String srcPath, String destPath) {
        srcPath = this.normalizePath(srcPath);
        destPath = this.normalizePath(destPath);
        Assert.isTrue(!srcPath.equals(destPath), "原路径不能与目标路径一样");
        CopyObjectResponse res = s3Client.copyObject(
                CopyObjectRequest.builder()
                    .sourceBucket(bucket)
                    .sourceKey(srcPath)
                    .destinationBucket(bucket)
                    .destinationKey(destPath)
                    .build()
        );
        log.debug("[{}]复制文件，srcPath={}, destPath={}, response={}", bucket, srcPath, destPath, res);
        return res;
    }

    /**
     * 复制目录。s3没有目录的概念，因此采用循环复制该路径前缀的文件（相当于子文件）实现
     *
     * @param srcPath
     * @param destPath
     * @return
     */
    public List<CopyObjectResponse> copyDirectory(String srcPath, String destPath) {
        srcPath = this.directoryStylePath(this.normalizePath(srcPath));
        destPath = this.directoryStylePath(this.normalizePath(destPath));
        Assert.isTrue(!srcPath.equals(destPath), "源路径不能与目标路径相同");
        // 防止目标是源的子路径（简单保护）
        Assert.isTrue(!destPath.startsWith(srcPath), "目标路径不能是源路径的子路径");

        List<CopyObjectResponse> result = new ArrayList<>(100);

        // 分页循环复制
        for (ListObjectsV2Response response : this.listObjectsPaginator(srcPath)) {
            if (!response.hasContents()) {
                continue;
            }
            for (S3Object obj : response.contents()) {
                String srcKey = obj.key();
                String destKey = replacePrefix(srcKey, srcPath, destPath);
                result.add(this.copy(srcKey, destKey));
            }
        }

        log.debug("[{}]复制目录，srcPath={}, destPath={}, 文件数={}", bucket, srcPath, destPath, result.size());
        return result;
    }

    /**
     * 移动文件，也可当做重命名使用。s3没有目录的概念，不支持目录。s3没有移动文件的api，因此采用先复制后删除的非原子实现
     *
     * @param srcPath
     * @param destPath
     * @return
     */
    public CopyObjectResponse move(String srcPath, String destPath) {
        // s3没有移动文件的api，因此使用复制文件后删掉原文件实现
        CopyObjectResponse res = this.copy(srcPath, destPath);
        // 此处没法原子实现，最坏情况可能复制成功了但是原文件没删除
        this.delete(srcPath);
        return res;
    }

    /**
     * 移动目录。s3没有目录的概念，因此采用循环移动该路径前缀的文件（相当于子文件）实现。s3没有移动文件的api，因此采用先复制后删除的非原子实现
     *
     * @param srcPath
     * @param destPath
     * @return
     */
    public List<CopyObjectResponse> moveDirectory(String srcPath, String destPath) {
        srcPath = this.directoryStylePath(this.normalizePath(srcPath));
        destPath = this.directoryStylePath(this.normalizePath(destPath));
        Assert.isTrue(!srcPath.equals(destPath), "源路径不能与目标路径相同");
        // 防止目标是源的子路径（简单保护）
        Assert.isTrue(!destPath.startsWith(srcPath), "目标路径不能是源路径的子路径");

        List<CopyObjectResponse> result = new ArrayList<>(100);

        // 分页循环复制
        for (ListObjectsV2Response response : this.listObjectsPaginator(srcPath)) {
            if (!response.hasContents()) {
                continue;
            }
            for (S3Object obj : response.contents()) {
                String srcKey = obj.key();
                String destKey = replacePrefix(srcKey, srcPath, destPath);
                result.add(this.move(srcKey, destKey));
            }
        }

        log.debug("[{}]移动目录，srcPath={}, destPath={}, 文件数={}", bucket, srcPath, destPath, result.size());
        return result;
    }

    /**
     * 删除文件，s3没有目录概念，不支持目录。删除成功返回删除结果，如果文件本身就不存在返回null也可以算作成功，失败会抛异常
     *
     * @param path
     * @return
     */
    public DeleteObjectResponse delete(String path) {
        path = this.normalizePath(path);
        try {
            DeleteObjectResponse res = s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(path)
                        .build()
            );
            log.debug("[{}]删除文件，path={}, result={}", bucket, path, res);
            return res;
        } catch (NoSuchKeyException e) {
            log.warn("[{}]忽略删除文件，不存在文件：{}", bucket, path, e);
            return null;
        }
    }

    /**
     * 删除目录。因s3没有目录的概念，因此采用循环删除所有含有该前缀的文件（相当于子文件）实现
     *
     * @param path
     * @return
     */
    public DeleteObjectsResponse deleteDirectory(String path) {
        List<DeletedObject> allDeleted = new ArrayList<>();
        List<S3Error> allErrors = new ArrayList<>();
        // s3没有目录的概念，因此只能列出子文件循环删除
        // 自动翻页删除
        for (ListObjectsV2Response response : this.listObjectsPaginator(path)) {
            if (!response.hasContents()) {
                continue;
            }
            List<String> paths = response.contents().stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());
            // 批量删除当前页
            DeleteObjectsResponse res = this.deleteBatch(paths);
            if (CollUtil.isNotEmpty(res.deleted())) {
                allDeleted.addAll(res.deleted());
            }
            if (CollUtil.isNotEmpty(res.errors())) {
                allErrors.addAll(res.errors());
            }
        }
        // 将分批收集的结果统一聚合回单个 DeleteObjectsResponse 对象
        DeleteObjectsResponse result = DeleteObjectsResponse.builder()
                .deleted(allDeleted.isEmpty() ? null : allDeleted)
                .errors(allErrors.isEmpty() ? null : allErrors)
                .build();
        log.debug("[{}]删除目录，path={}, result={}", bucket, path, result);
        return result;
    }

    /**
     * 批量删除。s3没有目录的概念，不支持目录
     *
     * @param paths
     * @return
     */
    public DeleteObjectsResponse deleteBatch(String... paths) {
        return this.deleteBatch(Arrays.asList(paths));
    }

    /**
     * 批量删除。s3没有目录的概念，不支持目录
     *
     * @param paths
     * @return
     */
    public DeleteObjectsResponse deleteBatch(Collection<String> paths) {
        return this.deleteBatch(paths, false);
    }

    /**
     * 批量删除，可选参数是否静默模式，即是否只返回失败的不包含删除成功的。s3没有目录的概念，不支持目录
     *
     * @param paths
     * @return
     */
    public DeleteObjectsResponse deleteBatch(Collection<String> paths, boolean quiteMode) {
        List<DeletedObject> allDeleted = new ArrayList<>(100);
        List<S3Error> allErrors = new ArrayList<>(100);
        // s3批量操作每次最多1000条，超过1000条的分批处理
        List<List<String>> splitList = CollUtil.partition(paths, BATCH_SIZE);
        int i = 1;
        for (List<String> list : splitList) {
            // 构建待删除的对象列表
            List<ObjectIdentifier> objectsToDelete = list.stream()
                    .map(path -> ObjectIdentifier.builder().key(this.normalizePath(path)).build())
                    .collect(Collectors.toList());

            DeleteObjectsResponse res = s3Client.deleteObjects(
                    DeleteObjectsRequest.builder()
                        .bucket(bucket)
                        .delete(Delete.builder()
                                .objects(objectsToDelete)
                                // quiet静默模式，不返回成功列表减少带宽，只返回失败列表
                                .quiet(quiteMode)
                                .build()
                        )
                        .build()
            );
            if (CollUtil.isNotEmpty(res.deleted())) {
                allDeleted.addAll(res.deleted());
            }
            if (CollUtil.isNotEmpty(res.errors())) {
                allErrors.addAll(res.errors());
            }
            log.debug("[{}]分批批量删除文件，批次：{}，结果列表：{}", bucket, i, res);
            i++;
        }

        // 将分批收集的结果统一聚合回单个 DeleteObjectsResponse 对象
        return DeleteObjectsResponse.builder()
                .deleted(allDeleted.isEmpty() ? null : allDeleted)
                .errors(allErrors.isEmpty() ? null : allErrors)
                .build();
    }

    /**
     * 上传本地文件
     *
     * @param path
     * @param file
     * return
     */
    public PutObjectResponse upload(String path, File file) {
        Assert.isTrue(!file.isDirectory(), "不支持上传文件夹");
        path = this.normalizePath(path);
        PutObjectResponse res = s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(path)
                        .contentType(MimeType.guessMimeType(file))
                        .build(),
                RequestBody.fromFile(file)
        );
        log.debug("[{}]上传本地文件，file={}, path={}, result={}", bucket, file, path, res);
        return res;
    }

    /**
     * 上传本地文件
     *
     * @param path
     * @param localPath
     * @return
     */
    public PutObjectResponse upload(String path, String localPath) {
        return this.upload(path, new File(localPath));
    }

    /**
     * 上传内存中的文件
     *
     * @param path
     * @param bytes
     * @return
     */
    public PutObjectResponse upload(String path, byte[] bytes) {
        return this.upload(path, bytes, MimeType.guessMimeType(path));
    }

    /**
     * 上传内存中的文件
     *
     * @param path
     * @param bytes
     * @param mimeType
     * @return
     */
    public PutObjectResponse upload(String path, byte[] bytes, String mimeType) {
        path = this.normalizePath(path);
        PutObjectResponse res = s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(path)
                        .contentType(mimeType)
                        .build(),
                RequestBody.fromBytes(bytes)
        );
        log.debug("[{}]上传bytes文件, path={}, result={}", bucket, path, res);
        return res;
    }

    /**
     * 上传文件流，会自动关闭输入流
     *
     * @param path
     * @param inputStream
     * @param size
     * @return
     */
    public PutObjectResponse upload(String path, InputStream inputStream, long size) {
        return this.upload(path, inputStream, size, MimeType.guessMimeType(path));
    }

    /**
     * 上传文件流，会自动关闭输入流
     *
     * @param path
     * @param inputStream
     * @param size
     * @return
     */
    public PutObjectResponse upload(String path, InputStream inputStream, long size, String mimeType) {
        path = this.normalizePath(path);
        try {
            PutObjectResponse res = s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(path)
                            .contentType(mimeType)
                            .build(),
                    RequestBody.fromInputStream(inputStream, size)
            );
            log.debug("[{}]上传inputStream文件, path={}, size={}, result={}", bucket, path, size, res);
            return res;
        } finally {
            IoUtil.closeQuietly(inputStream);
        }
    }

    /**
     * 上传未知大小文件流，会自动关闭文件流，尽量避免使用
     *
     * @param path
     * @param inputStream
     * @return
     */
    public PutObjectResponse upload(String path, InputStream inputStream) {
        return this.upload(path, inputStream, MimeType.guessMimeType(path));
    }

    /**
     * 上传未知大小文件流，会自动关闭文件流，尽量避免使用
     *
     * @param path
     * @param inputStream
     * @param mimeType
     * @return
     */
    public PutObjectResponse upload(String path, InputStream inputStream, String mimeType) {
        path = this.normalizePath(path);
        try {
            PutObjectResponse res = s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(path)
                            .contentType(mimeType)
                            .build(),
                    RequestBody.fromContentProvider(() -> inputStream, mimeType));
            log.debug("[{}]上传inputStream文件, path={}, result={}", bucket, path, res);
            return res;
        } finally {
            IoUtil.closeQuietly(inputStream);
        }
    }

    /**
     * 下载成文件流，用完流记得关闭
     *
     * @param path
     * @return
     */
    public ResponseInputStream<GetObjectResponse> download(String path) {
        path = this.normalizePath(path);
        return s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(path)
                        .build()
        );
    }

    /**
     * 断点下载成文件流，用完流记得关闭
     *
     * @param path
     * @param offset
     * @param length
     * @return
     */
    public ResponseInputStream<GetObjectResponse> download(String path, long offset, long length) {
        Assert.isTrue(offset >= 0, "offset必须大于等于0");
        Assert.isTrue(length > 0, "length必须大于0");
        path = this.normalizePath(path);
        String range = "bytes=" + offset + "-" + (offset + length - 1);
        return s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(path)
                    .range(range)
                    .build()
        );
    }

    /**
     * 下载到输出流
     *
     * @param path
     * @param outputStream
     * @return
     * @throws IOException
     */
    public long download(String path, OutputStream outputStream) throws IOException {
        try (InputStream in = this.download(path)) {
            return in.transferTo(outputStream);
        }
    }

    /**
     * 断点下载到输出流
     *
     * @param path
     * @param offset
     * @param length
     * @param outputStream
     * @return
     * @throws IOException
     */
    public long download(String path, long offset, long length, OutputStream outputStream) throws IOException {
        try (InputStream in = this.download(path, offset, length)) {
            return in.transferTo(outputStream);
        }
    }

    /**
     * 下载到本地文件
     *
     * @param path
     * @param file
     * @return
     * @throws IOException
     */
    public long download(String path, File file) throws IOException {
        try (OutputStream os = new FileOutputStream(file)) {
            return this.download(path, os);
        }
    }

    /**
     * 下载到本地文件
     *
     * @param path
     * @param localPath
     * @return
     * @throws IOException
     */
    public long download(String path, String localPath) throws IOException {
        return this.download(path, new File(localPath));
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
        // \替换为/
        path = path.replace('\\', '/');
        // 重复的/替换为单个/
        path = path.replaceAll("//+", "/");
        // 防止路径遍历攻击，如果包含 ../ 则抛出异常或进行过滤
        if (path.contains("../")) {
            throw new IllegalArgumentException("路径包含非法字符 ../，禁止路径遍历");
        }
        // aws s3不应该以/开头
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * 路径是否是文件夹，即是否/结尾
     *
     * @param path
     * @return
     */
    public boolean isPathDirectoryStyle(String path) {
        return path.endsWith("/");
    }

    /**
     * 转为文件夹路径，即/结尾
     *
     * @param path
     * @return
     */
    private String directoryStylePath(String path) {
        return this.isPathDirectoryStyle(path) ? path : path + "/";
    }

    /**
     * 将源路径下的对象 key 映射到目标路径
     * 例：srcPrefix = "a/b/", destPrefix = "x/y/", key = "a/b/c.txt" → "x/y/c.txt"
     */
    public String replacePrefix(String path, String srcPrefix, String destPrefix) {
        if (!path.startsWith(srcPrefix)) {
            throw new IllegalArgumentException("不以源前缀开头: " + path);
        }
        Assert.isTrue(path.startsWith(srcPrefix), path + " 不以源前缀 " + srcPrefix + " 开头");
        return destPrefix + path.substring(srcPrefix.length());
    }

}
