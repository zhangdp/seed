package io.github.seed.common.component;

import io.github.seed.common.config.FileStorageProperties;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.MimeType;
import io.github.seed.common.exception.NotFoundException;
import io.github.seed.common.util.WebUtils;
import io.github.seed.entity.sys.FileInfo;
import io.github.seed.model.dto.FileInfoDto;
import io.github.seed.service.sys.FileInfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.io.IoUtil;
import org.dromara.hutool.core.net.url.UrlEncoder;
import org.dromara.hutool.crypto.SecureUtil;
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
        String extension = this.getFilenameExtension(fileName);
        // 实际路径以日期分文件夹，文件名以文件ID+后缀保存，防止文件名重复覆盖
        String remotePath = fileProperties.getRootPath() + (fileProperties.getRootPath().endsWith("/") ? "" : "/")
                + now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth() + "/" + fileId + extension;

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
        dto.setExpireTime(entity.getExpireTime() == null ? Const.MAX_LOCAL_DATE_TIME : entity.getExpireTime());
        return dto;
    }

    /**
     * 下载文件
     *
     * @param response
     * @param fileId
     * @param isInline
     * @param customFilename
     */
    @SneakyThrows
    public void doDownload(HttpServletResponse response, String fileId, boolean isInline, String customFilename) {
        FileInfo fileInfo = fileInfoService.getByFileId(fileId);
        if (fileInfo == null) {
            throw new NotFoundException("不存在文件id为" + fileId + "的文件记录");
        }
        InputStream in = fileTemplate.download(fileInfo.getStoragePath());
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

    /**
     * 获取包含.的文件名后缀
     *
     * @param fileName
     * @return
     */
    public String getFilenameExtension(String fileName) {
        return this.getFilenameExtension(fileName, true);
    }

    /**
     * 获取文件名后缀
     *
     * @param fileName
     * @param includeDot
     * @return
     */
    public String getFilenameExtension(String fileName, boolean includeDot) {
        int index = fileName.lastIndexOf('.');
        // 没有后缀时返回空字符串，如果第一个字符是.不是表示后缀而是表示隐藏文件如.gitignore文件是隐藏文件且后缀是空
        if (index <= 0) {
            return "";
        }
        return fileName.substring(index + (includeDot ? 0 : 1)).toLowerCase();
    }

}
