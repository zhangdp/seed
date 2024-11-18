package io.github.seed.controller;

import io.github.seed.common.component.FileManager;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.common.util.WebUtils;
import io.github.seed.model.dto.FileInfoDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 2024/11/8 文件接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileManager fileManager;

    /**
     * 上传附件
     *
     * @param file
     * @param loginUser
     * @return
     */
    @PostMapping("/upload")
    public FileInfoDto upload(MultipartFile file, LoginUser loginUser) {
        return fileManager.doUpload(file, loginUser.getId());
    }

    /**
     * 下载文件
     *
     * @param fileId
     * @param response
     * @param inline
     * @param filename
     */
    @GetMapping("/download/{fileId}/**")
    public void download(HttpServletResponse response, @PathVariable String fileId, String inline, String filename) {
        fileManager.doDownload(response, fileId, WebUtils.isParamTrue(inline), filename);
    }
}
