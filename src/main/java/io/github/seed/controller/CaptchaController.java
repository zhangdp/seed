package io.github.seed.controller;

import io.github.seed.manager.CaptchaManager;
import io.github.seed.module.captcha.CaptchaService;
import io.github.seed.module.security.data.IgnoreAuth;
import io.github.seed.model.dto.ImageCaptcha;
import io.github.seed.model.dto.SmsCaptchaParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    private final CaptchaService captchaService;
    private final CaptchaManager captchaManager;

    /**
     * 图片验证码
     * <br>注意：scene必须与校验端{@code @VerifyCaptcha(scene = ...)}一致，否则校验必然失败，
     * 例如密码登录使用{@code scene=login}（对应{@code CaptchaScene.LOGIN}）
     *
     * @param scene  场景，不同场景的验证码互相隔离，为空使用默认场景
     * @param width  图片宽度，为空使用默认值，超出配置范围会被夹取
     * @param height 图片高度，为空使用默认值，超出配置范围会被夹取
     * @return
     */
    @IgnoreAuth
    @GetMapping("/image")
    @Operation(summary = "获取图片验证码", description = "返回验证码标识key与图片base64，校验时需把key与用户输入的验证码一并提交。"
            + "scene必须与校验端的场景一致，密码登录请传scene=login")
    public ImageCaptcha imageCaptcha(@RequestParam(required = false) String scene,
                                     @RequestParam(required = false) Integer width,
                                     @RequestParam(required = false) Integer height) {
        return captchaService.generateImage(scene, width, height);
    }

    /**
     * 发送短信验证码
     *
     * @param params
     * @return
     */
    @IgnoreAuth
    @PostMapping("/sms")
    @Operation(summary = "发送短信验证码", description = "同一场景下同一手机号在发送间隔内不允许重复发送，不同场景互不影响")
    public boolean smsCaptcha(@RequestBody @Valid SmsCaptchaParams params) {
        captchaManager.sendSms(params.getScene(), params.getMobile());
        return true;
    }
}
