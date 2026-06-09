package io.github.seed.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 *
 * @author zhangdp
 * @since 2026/5/26
 */
@Data
@Schema(title = "图形验证码")
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
    @Schema(title = "图片base64")
    private String image;
}
