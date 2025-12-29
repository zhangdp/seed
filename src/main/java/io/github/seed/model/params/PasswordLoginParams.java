package io.github.seed.model.params;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 密码登录入参
 *
 * @author zhangdp
 * @since 2024/6/26
 */
@Deprecated
@Data
@Schema(title = "密码登录")
public class PasswordLoginParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 账号名，也可以是手机号、邮箱
     */
    @NotBlank(message = "账号不能为空")
    @Schema(title = "账号", description = "也可以是手机号、邮箱")
    private String username;
    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Schema(title = "密码", description = "需要rsa加密")
    private String password;

}
