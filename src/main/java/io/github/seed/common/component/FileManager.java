package io.github.seed.common.component;

import cn.hutool.v7.core.bean.BeanUtil;
import cn.hutool.v7.core.date.DateUtil;
import cn.hutool.v7.core.date.TimeUtil;
import cn.hutool.v7.core.io.IoUtil;
import cn.hutool.v7.core.io.file.FileNameUtil;
import cn.hutool.v7.core.net.url.UrlEncoder;
import cn.hutool.v7.core.util.ObjUtil;
import cn.hutool.v7.crypto.SecureUtil;
import io.github.seed.common.config.FileStorageProperties;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.MimeType;
import io.github.seed.common.exception.NotFoundException;
import io.github.seed.common.util.WebUtils;
import io.github.seed.entity.sys.FileInfo;
import io.github.seed.model.dto.FileInfoDto;
import io.github.seed.service.sys.FileInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 2024/11/8 附件管理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileManager {

    private final FileInfoService fileInfoService;
    private final FileTemplate fileTemplate;
    private final FileStorageProperties fileProperties;

    /**
     * 上传文件
     *
     * @param file
     * @param uploadUserId
     * @return
     */
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public FileInfoDto doUpload(MultipartFile file, Long uploadUserId) {
        LocalDateTime now = LocalDateTime.now();
        long ttl = fileProperties.getExpires().toMillis();
        LocalDateTime expireTime = ttl > 0 ? LocalDateTime.now().plus(ttl, ChronoUnit.MILLIS) : Const.MAX_LOCAL_DATE_TIME;
        String fileId = fileInfoService.generateId();
        String fileName = file.getOriginalFilename();
        String extension = FileNameUtil.extName(fileName);
        if (extension == null) {
            extension = "";
        } else if (!extension.isEmpty()) {
            extension = "." + extension;
        }
        // 实际路径以日期分文件夹，文件名以文件ID+后缀保存，防止文件名重复覆盖
        String remotePath = fileProperties.getRootPath() + (fileProperties.getRootPath().endsWith("/") ? "" : "/")
                + TimeUtil.format(now.toLocalDate(), "yyyy-MM/dd") + "/" + fileId + extension;

        // 记录保存到数据库
        FileInfo entity = new FileInfo();
        entity.setFileId(fileId);
        entity.setUserId(uploadUserId);
        entity.setFileName(fileName);
        entity.setExtension(extension);
        entity.setMimeType(MimeType.getType(extension));
        entity.setFileSize(file.getSize());
        entity.setFileHash(this.calculateSHA256(file.getInputStream()));
        entity.setStoragePath(remotePath);
        entity.setDownloadUrl(this.generateDownloadUrl(fileId, fileName));
        entity.setUploadTime(now);
        entity.setExpireTime(expireTime);
        fileInfoService.add(entity);

        // 保存文件
        fileTemplate.upload(file.getInputStream(), remotePath);

        FileInfoDto dto = new FileInfoDto();
        BeanUtil.copyProperties(entity, dto);
        return dto;
    }

    /**
     * 下载文件
     *
     * @param request
     * @param response
     * @param fileId
     * @param isInline
     * @param customFilename
     */
    @SneakyThrows
    public void doDownload(HttpServletRequest request, HttpServletResponse response, String fileId, boolean isInline, String customFilename) {
        FileInfo fileInfo = fileInfoService.getByFileId(fileId);
        if (fileInfo == null) {
            throw new NotFoundException("不存在文件id为" + fileId + "的文件记录");
        }
        String etag = "\"" + fileInfo.getFileHash() + "\"";
        // 上次修改时间，http时间只精确到秒
        long lastModified = TimeUtil.toEpochMilli(ObjUtil.defaultIfNull(fileInfo.getUpdatedAt(), fileInfo.getCreatedAt())) / 1000L * 1000L;
        if (fileProperties.isHttpCacheable() && WebUtils.checkNotModified(request, etag, lastModified)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            log.debug("文件未改变，返回304，fileId={}", fileInfo.getFileId());
            return;
        }
        InputStream in = fileTemplate.download(fileInfo.getStoragePath());
        if (fileProperties.isHttpCacheable()) {
            WebUtils.responseCacheHeader(response, "public, max-age=" + fileProperties.getHttpCacheMaxAge() + ", immutable", lastModified, etag);
        }
        WebUtils.responseFile(response, in, fileInfo.getFileName(), fileInfo.getFileSize(), fileInfo.getMimeType(), isInline);
    }

    /**
     * 计算文件sha-256
     *
     * @param inputStream
     * @return
     */
    public String calculateSHA256(InputStream inputStream) {
        try {
            return SecureUtil.sha256(inputStream);
        } finally {
            IoUtil.closeQuietly(inputStream);
        }
    }

    /**
     * 生成下载路径
     *
     * @param fileId
     * @param fileName
     * @return
     */
    public String generateDownloadUrl(String fileId, String fileName) {
        return fileProperties.getDownloadUrl().replace("{fileId}", fileId)
                .replace("{fileName}", UrlEncoder.encodeQuery(fileName));
    }

}
