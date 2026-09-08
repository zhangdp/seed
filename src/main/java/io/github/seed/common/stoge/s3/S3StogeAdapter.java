package io.github.seed.common.stoge.s3;

import io.github.seed.common.stoge.StogeData;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BadRequestException;
import io.github.seed.common.exception.InternalServerException;
import io.github.seed.common.util.MimeType;
import io.github.seed.common.stoge.StogeAdapter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.http.ContentStreamProvider;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Error;

import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * aws s3标准协议文件访问器，也兼容minio、oos、ocs等类s3协议
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class S3StogeAdapter implements StogeAdapter {

    private final S3Template s3Template;

    @Override
    public StogeData upload(String path, InputStream inputStream, long size, String fileName) {
        if (fileName == null) {
            fileName = path.substring(path.lastIndexOf('/') + 1);
        }
        String mimeType = MimeType.getMimeType(fileName);
        PutObjectResponse res = s3Template.upload(path, inputStream, size, mimeType);
        StogeData data = new StogeData();
        data.setFileName(fileName);
        data.setMimeType(mimeType);
        data.setETag(res.eTag().replace("\"", ""));
        // 单个文件上传etag就是文件md5
        data.setChecksum(data.getETag());
        data.setLastModified(System.currentTimeMillis());
        // data.setExpire();
        data.setSize(size);
        //data.setMetadata();
        return data;
    }

    @Override
    public StogeData upload(String path, MultipartFile file) {
        // 如果开启了分段校验传输，则直接上传文件流
        if (s3Template.isChunkedEncodingEnabled()) {
            return StogeAdapter.super.upload(path, file);
        }
        // 否则需要构造可重新读取流的Provider上传，因为s3需要重新读取流去实现重试、校验等操作
        else {
            String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : path.substring(path.lastIndexOf('/') + 1);
            String mimeType = MimeType.guessMimeType(fileName);
            long size = file.getSize();
            // 构造流提供器
            ContentStreamProvider streamProvider = ContentStreamProvider.fromInputStreamSupplier(() -> {
                try {
                    // 这里只是刚拿到流还未开始读不能手动关闭，fromInputStreamSupplier内部会自动关闭
                    return file.getInputStream();
                } catch (IOException e) {
                    throw new BadRequestException(ErrorCode.REQUEST_BODY_NOT_READABLE.code(), file.getOriginalFilename() + "上传失败", e);
                }
            });
            PutObjectResponse res = s3Template.upload(path, streamProvider, mimeType, size);
            StogeData data = new StogeData();
            data.setFileName(fileName);
            data.setMimeType(mimeType);
            data.setETag(res.eTag().replace("\"", ""));
            // 单个文件上传etag就是文件md5
            data.setChecksum(data.getETag());
            data.setLastModified(System.currentTimeMillis());
            // data.setExpire();
            data.setSize(file.getSize());
            //data.setMetadata();
            return data;
        }
    }

    @Override
    public StogeData upload(String path, File file) {
        String fileName = file.getName();
        PutObjectResponse res = s3Template.upload(path, file);
        StogeData data = new StogeData();
        data.setFileName(file.getName());
        data.setMimeType(MimeType.getMimeType(fileName));
        data.setETag(res.eTag().replace("\"", ""));
        // 单个文件上传etag就是文件md5
        data.setChecksum(data.getETag());
        data.setLastModified(System.currentTimeMillis());
        data.setExpire(null);
        data.setSize(file.length());
        data.setMetadata(null);
        return data;
    }

    @Override
    public InputStream download(String path) {
        return s3Template.download(path);
    }

    @Override
    public long download(String path, OutputStream out) {
        try {
            return s3Template.download(path, out);
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.S3_ERROR.code(), "下载文件失败", e);
        }
    }

    @Override
    public long download(String path, File file) {
        try {
            return s3Template.download(path, file);
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.S3_ERROR.code(), "下载文件失败", e);
        }
    }

    @Override
    public boolean delete(String path) {
        return s3Template.delete(path) != null;
    }

    @Override
    public List<String> deleteBatch(Collection<String> paths) {
        DeleteObjectsResponse res = s3Template.deleteBatch(paths);
        return res.errors() != null ? res.errors().stream().map(S3Error::key).toList() : Collections.emptyList();
    }

}
