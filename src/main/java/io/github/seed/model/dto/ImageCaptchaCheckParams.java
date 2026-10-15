package io.github.seed.model.dto;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 行为验证码校验入参
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "行为验证码校验")
public class ImageCaptchaCheckParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码id，来自获取验证码接口返回
     */
    @NotBlank(message = "验证码id不能为空")
    @Schema(title = "验证码id", description = "来自GET /captcha/image返回的id")
    private String id;
    /**
     * 场景，必须与业务接口上@VerifyCaptcha(scene=...)一致，如密码登录传login
     */
    @NotBlank(message = "场景不能为空")
    @Schema(title = "场景", description = "必须与业务接口的场景一致，密码登录传login")
    private String scene;
    /**
     * 用户操作轨迹，由前端验证码组件采集
     */
    @NotNull(message = "验证码轨迹不能为空")
    @Schema(title = "操作轨迹", description = "用户完成滑块/旋转/点选等交互后由前端组件采集")
    private ImageCaptchaTrack track;

}
