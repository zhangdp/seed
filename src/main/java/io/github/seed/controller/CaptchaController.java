package io.github.seed.controller;

import io.github.seed.manager.CaptchaManager;
import io.github.seed.model.dto.BehaviorCaptchaCheckParams;
import io.github.seed.model.dto.SmsCaptchaParams;
import io.github.seed.module.captcha.CaptchaService;
import io.github.seed.module.captcha.data.BehaviorCaptcha;
import io.github.seed.module.captcha.data.CaptchaTicket;
import io.github.seed.module.captcha.data.ImageCaptcha;
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
    @GetMapping("/behavior")
    @Operation(summary = "获取行为验证码", description = "返回id、类型与图片base64，前端按type渲染交互组件；"
            + "用户完成交互后调POST /captcha/behavior/check换取一次性票据")
    public BehaviorCaptcha behaviorCaptcha(@RequestParam(required = false) String type) {
        return captchaService.generateBehavior(type);
    }

    /**
     * 校验行为验证码，成功返回一次性票据
     *
     * @param params 验证码id、场景与用户操作轨迹
     * @return 一次性票据
     */
    @IgnoreAuth
    @PostMapping("/behavior/check")
    @Operation(summary = "校验行为验证码", description = "校验用户操作轨迹，成功返回一次性票据{key, code}；"
            + "校验是一次性的，失败须重新获取验证码。scene必须与业务接口的场景一致，密码登录传login")
    public CaptchaTicket checkBehaviorCaptcha(@RequestBody @Valid BehaviorCaptchaCheckParams params) {
        return captchaService.checkBehavior(params.getScene(), params.getId(), params.getTrack());
    }

    /**
     * 获取图片验证码
     *
     * @param scene  场景，如login，为空使用默认场景
     * @param width  图片宽度，为空使用配置值
     * @param height 图片高度，为空使用配置值
     * @return 图片验证码
     * @deprecated 改用GET /captcha/behavior，字符型图片验证码易被OCR识别
     */
    @Deprecated
    @IgnoreAuth
    @GetMapping("/image")
    @Operation(summary = "获取图片验证码", description = "已废弃，改用GET /captcha/behavior；"
            + "保留仅为兼容旧前端。返回key与图片base64，用户输入的字符作为code提交")
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
