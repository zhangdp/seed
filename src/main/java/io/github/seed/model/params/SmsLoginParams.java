package io.github.seed.model.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2025/12/22 短信登录入参
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Deprecated
@Data
@Schema(title = "短信登录入参")
public class SmsLoginParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Schema(title = "手机号")
    private String mobile;
    /**
     * 短信验证码
     */
    @NotBlank(message = "短信验证码不能为空")
    @Schema(title = "短信验证码")
    private String code;
}
