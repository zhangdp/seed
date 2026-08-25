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
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Error;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    public StogeData upload(String path, MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            String mimeType = MimeType.guessMimeType(fileName);
            PutObjectResponse res = s3Template.upload(path, in, file.getSize(), mimeType);
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
        } catch (IOException e) {
            throw new BadRequestException(ErrorCode.REQUEST_BODY_NOT_READABLE.code(), file.getOriginalFilename() + "上传失败", e);
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
