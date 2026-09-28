package io.github.seed.module.captcha.data;

/**
 * 验证码场景常量
 * <br>场景会作为redis key中的一段，用于隔离不同业务的验证码以及发送频率限制，
 * 因此同一手机号在登录场景和重置密码场景的发送间隔互不影响
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface CaptchaScene {

    /**
     * 默认场景，图片验证码未指定场景时使用
     */
    String DEFAULT = "default";
    /**
     * 登录
     */
    String LOGIN = "login";
    /**
     * 注册
     */
    String REGISTER = "register";
    /**
     * 重置密码
     */
    String RESET_PASSWORD = "reset_password";
    /**
     * 绑定手机号
     */
    String BIND_MOBILE = "bind_mobile";
    /**
     * 修改手机号
     */
    String CHANGE_MOBILE = "change_mobile";
}
