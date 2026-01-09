package io.github.seed.common.component;

import cn.hutool.v7.core.bean.BeanUtil;
import cn.hutool.v7.core.date.TimeUtil;
import cn.hutool.v7.core.io.IoUtil;
import cn.hutool.v7.core.io.file.FileNameUtil;
import cn.hutool.v7.core.text.StrUtil;
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

/**
 * 附件管理器
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
        // 如果是正的表示有过期时间，否则永不过期
        LocalDateTime expireTime = fileProperties.getExpireDuration() != null && fileProperties.getExpireDuration().isPositive()
                ? LocalDateTime.now().plus(fileProperties.getExpireDuration())
                : Const.MAX_LOCAL_DATE_TIME;
        // 附件唯一id
        String fileId = fileInfoService.generateId();
        String fileName = file.getOriginalFilename();
        String extension = StrUtil.defaultIfNull(FileNameUtil.extName(fileName), "").trim();
        // 根目录
        String remotePath = fileProperties.getRootPath() != null ? fileProperties.getRootPath().trim() : "";
        if (remotePath.endsWith("/")) {
            remotePath += "/";
        }
        // 按日期分文件夹
        remotePath += TimeUtil.format(now.toLocalDate(), "yyyy-MM/dd") + "/";
        // 文件名为附件唯一id防止重复
        remotePath += fileId;
        // 后缀
        if (!extension.isEmpty()) {
            remotePath += "." + extension;
        }

        // 记录保存到数据库
        FileInfo entity = new FileInfo();
        entity.setFileId(fileId);
        entity.setUserId(uploadUserId);
        entity.setFileName(fileName);
        entity.setExtension(extension);
        entity.setMimeType(MimeType.getType(extension));
        entity.setSize(file.getSize());
        entity.setHash(this.calculateSHA256(file.getInputStream()));
        entity.setStoragePath(remotePath);
        entity.setDownloadUrl(this.generateDownloadUrl(fileId, fileName));
        entity.setUploadAt(now);
        entity.setExpireAt(expireTime);
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
     * @param fileName
     */
    @SneakyThrows
    public void doDownload(HttpServletRequest request, HttpServletResponse response, String fileId, boolean isInline, String fileName) {
        // 从数据库取出附件信息
        FileInfo fileInfo = fileInfoService.getByFileId(fileId);
        if (fileInfo == null) {
            throw new NotFoundException("ID为" + fileId + "的附件记录不存在");
        }
        // 附件已过期不允许下载
        if (fileInfo.getExpireAt().isBefore(LocalDateTime.now())) {
            throw new NotFoundException("ID为" + fileId + "的附件已过期删除");
        }
        // 如果有开启http缓存且未修改直接返回304
        String etag = "\"" + fileInfo.getHash() + "\"";
        // 上次修改时间，http时间只精确到秒
        long lastModified = TimeUtil.toEpochMilli(ObjUtil.defaultIfNull(fileInfo.getUpdatedAt(), fileInfo.getCreatedAt())) / 1000L * 1000L;
        if (fileProperties.isHttpCacheable() && WebUtils.checkNotModified(request, etag, lastModified)) {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            log.debug("文件未改变，返回304，fileId={}", fileInfo.getFileId());
            return;
        }
        // 如果开启http缓存都设置http缓存头
        if (fileProperties.isHttpCacheable()) {
            WebUtils.responseCacheHeader(response, "public, max-age=" + fileProperties.getHttpCacheMaxAge() + ", immutable", lastModified, etag);
        }
        // 设置下载相关的http头
        WebUtils.responseDispositionHeader(response, StrUtil.defaultIfBlank(fileName, fileInfo.getFileName()), fileInfo.getSize(), fileInfo.getMimeType(), isInline);
        // 从远端读取文件并输出到输出流，无需flush()或者关闭输出流，web容器会自行处理
        fileTemplate.download(fileInfo.getStoragePath(), response.getOutputStream());
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
                .replace("{fileName}", WebUtils.urlEncode(fileName));
    }

}
