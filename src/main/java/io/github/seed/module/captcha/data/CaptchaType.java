package io.github.seed.module.captcha.data;

/**
 * 验证码类型
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum CaptchaType {

    /**
     * 行为验证码（滑块/旋转/拼接/文字点选），校验通过后签发的一次性票据也按本类型存储，
     * 因此业务接口上的{@code @VerifyCaptcha}校验票据时同样用本类型
     */
    BEHAVIOR("behavior"),
    /**
     * 短信验证码
     */
    SMS("sms"),
    /**
     * 图片验证码（字符型）
     *
     * @deprecated 改用{@link #BEHAVIOR}，字符型图片验证码易被OCR识别，仅为兼容旧前端保留
     */
    @Deprecated
    IMAGE("image");

    /**
     * 类型标识，同时作为redis key中的一段
     */
    private final String type;

    CaptchaType(String type) {
        this.type = type;
    }

    /**
     * 获取类型标识
     *
     * @return 类型标识
     */
    public String type() {
        return this.type;
    }
}
