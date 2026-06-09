package io.github.seed.controller;

import io.github.seed.common.annotation.IgnoreAuth;
import io.github.seed.common.annotation.NoWrapperResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 验证码接口
 *
 * @author zhangdp
 * @since 2026/5/26
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/captcha")
@Tag(name = "验证码", description = "验证码接口")
public class CaptchaController {

    /**
     * 图片验证码
     *
     * @param width
     * @param height
     */
    @NoWrapperResponse
    @IgnoreAuth
    @GetMapping("/image")
    public void imageCaptcha(@RequestParam(required = false, defaultValue = "200") int width,
                             @RequestParam(required = false, defaultValue = "50") int height) throws IOException {
        // todo
    }
}
