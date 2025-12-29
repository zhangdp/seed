package io.github.seed.model.params;

import io.github.seed.common.enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2025/12/22 登录入参
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "登录入参")
public class LoginParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录方式
     */
    @NotNull(message = "登录方式不能为空")
    @Schema(title = "登录方式", description = "password：账号密码；sms：短信验证码")
    private LoginType loginType;

    /**
     * 账号，也可以是手机号、邮箱
     */
    @NotBlank(message = "账号/手机号不能为空")
    @Schema(title = "账号/手机号", description = "也可以是邮箱")
    private String username;
    /**
     * 密码
     */
    // @NotBlank(message = "密码不能为空")
    @Schema(title = "密码", description = "需要rsa加密")
    private String password;
    /**
     * 验证码
     */
    @Schema(title = "验证码")
    private String code;
}
