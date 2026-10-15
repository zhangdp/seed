package io.github.seed.module.captcha.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 行为验证码（滑块/旋转/拼接/文字点选），前端按{@code type}渲染对应的交互组件
 * <br>图片均为带{@code data:image/png;base64,}前缀的base64，可直接用于img标签的src
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "行为验证码")
public class BehaviorCaptcha implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码id，校验时回传
     */
    @Schema(title = "验证码id")
    private String id;
    /**
     * 验证码类型，取值见{@code app.captcha.behavior.types}：SLIDER滑块、ROTATE旋转、
     * CONCAT拼接、WORD_IMAGE_CLICK文字点选
     */
    @Schema(title = "验证码类型")
    private String type;
    /**
     * 背景图base64
     */
    @Schema(title = "背景图base64")
    private String backgroundImage;
    /**
     * 模板图base64，滑块图的缺口块、旋转图的待旋转图；文字点选时为提示文字小图
     */
    @Schema(title = "模板图base64")
    private String templateImage;
    /**
     * 背景图宽度
     */
    @Schema(title = "背景图宽度")
    private Integer backgroundImageWidth;
    /**
     * 背景图高度
     */
    @Schema(title = "背景图高度")
    private Integer backgroundImageHeight;
    /**
     * 模板图宽度
     */
    @Schema(title = "模板图宽度")
    private Integer templateImageWidth;
    /**
     * 模板图高度
     */
    @Schema(title = "模板图高度")
    private Integer templateImageHeight;
    /**
     * 附加数据，文字点选时为待点选的文字列表
     */
    @Schema(title = "附加数据", description = "文字点选时为待点选的文字列表")
    private Object data;

}
