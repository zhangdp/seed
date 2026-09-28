package io.github.seed.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 发送短信验证码入参
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "发送短信验证码入参")
public class SmsCaptchaParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(title = "手机号")
    private String mobile;
    /**
     * 场景
     */
    @NotBlank(message = "场景不能为空")
    @Schema(title = "场景", description = "login：登录；register：注册；reset_password：重置密码；bind_mobile：绑定手机号")
    private String scene;

}
