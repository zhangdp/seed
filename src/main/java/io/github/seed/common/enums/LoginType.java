package io.github.seed.common.enums;

/**
 * 2023/4/17 登录类型
 *
 * @author zhangdp
 * @since 1.0.0
 */
public enum LoginType {

    /**
     * 密码
     */
    PASSWORD("password"),
    /**
     * 短信验证码
     */
    SMS("sms"),
    /**
     * 单点
     */
    SSO("sso");

    private final String type;

    LoginType(String type) {
        this.type = type;
    }

    public String type() {
        return this.type;
    }
}
