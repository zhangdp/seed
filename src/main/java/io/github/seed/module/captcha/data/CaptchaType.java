package io.github.seed.module.captcha.data;

/**
 * 验证码类型
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum CaptchaType {

    /**
     * 图片验证码
     */
    IMAGE("image"),
    /**
     * 短信验证码
     */
    SMS("sms");

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
