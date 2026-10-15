package io.github.seed.module.captcha.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图片验证码（字符型），前端展示图片、提交用户输入的字符
 *
 * @author zhangdp
 * @since 1.0.0
 * @deprecated 字符型图片验证码易被OCR识别，改用{@link BehaviorCaptcha}
 */
@Data
@Schema(title = "图形验证码")
@Deprecated
public class ImageCaptcha implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码标识
     */
    @Schema(title = "验证码标识")
    private String key;
    /**
     * 图片base64
     */
    @Schema(title = "图片base64", description = "带data:image/png;base64,前缀，可直接用于img标签的src")
    private String image;
}
