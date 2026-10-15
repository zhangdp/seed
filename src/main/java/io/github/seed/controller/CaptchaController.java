package io.github.seed.controller;

import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import io.github.seed.manager.CaptchaManager;
import io.github.seed.model.dto.ImageCaptchaCheckParams;
import io.github.seed.model.dto.SmsCaptchaParams;
import io.github.seed.module.captcha.CaptchaService;
import io.github.seed.module.captcha.data.CaptchaTicket;
import io.github.seed.module.security.annotation.IgnoreAuth;
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
     * 获取行为验证码（滑块/旋转/拼接/文字点选），按返回的type渲染对应的交互组件
     *
     * @param type 验证码类型，为空从启用的类型中随机
     * @return 验证码数据
     */
    @IgnoreAuth
    @GetMapping("/image")
    @Operation(summary = "获取行为验证码", description = "返回id、类型与图片base64，前端按type渲染交互组件；"
            + "用户完成交互后调POST /captcha/image/check换取一次性票据")
    public ImageCaptchaVO imageCaptcha(@RequestParam(required = false) String type) {
        return captchaService.generateImage(type);
    }

    /**
     * 校验行为验证码，成功返回一次性票据
     *
     * @param params 验证码id、场景与用户操作轨迹
     * @return 一次性票据
     */
    @IgnoreAuth
    @PostMapping("/image/check")
    @Operation(summary = "校验行为验证码", description = "校验用户操作轨迹，成功返回一次性票据{key, code}；"
            + "校验是一次性的，失败须重新获取验证码。scene必须与业务接口的场景一致，密码登录传login")
    public CaptchaTicket checkImageCaptcha(@RequestBody @Valid ImageCaptchaCheckParams params) {
        return captchaService.checkImage(params.getScene(), params.getId(), params.getTrack());
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
