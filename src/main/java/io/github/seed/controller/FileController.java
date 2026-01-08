package io.github.seed.controller;

import io.github.seed.common.component.FileManager;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.common.util.WebUtils;
import io.github.seed.model.dto.FileInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
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
@Tag(name = "文件", description = "上传、下载文件接口")
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
    @Operation(summary = "上传文件")
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
    @Operation(summary = "下载文件")
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable String fileId, @RequestParam(required = false) String inline, @RequestParam(required = false) String filename) {
        fileManager.doDownload(request, response, fileId, WebUtils.isParamTrue(inline), filename);
    }
}
