package io.github.seed.module.captcha;

import io.github.seed.module.captcha.data.CaptchaScene;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 验证码配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(CaptchaProperties.CONFIG_PREFIX)
public class CaptchaProperties {

    public static final String CONFIG_PREFIX = "app.captcha";

    /**
     * 图片验证码有效期
     */
    private Duration imageExpire = Duration.ofMinutes(5);
    /**
     * 图片验证码默认宽度
     */
    private int imageWidth = 200;
    /**
     * 图片验证码默认高度
     */
    private int imageHeight = 50;
    /**
     * 图片验证码允许的最小宽度，防止被构造超大图片
     */
    private int imageMinWidth = 80;
    /**
     * 图片验证码允许的最大宽度
     */
    private int imageMaxWidth = 400;
    /**
     * 图片验证码允许的最小高度
     */
    private int imageMinHeight = 30;
    /**
     * 图片验证码允许的最大高度
     */
    private int imageMaxHeight = 200;
    /**
     * 图片验证码字符个数
     */
    private int imageCodeLength = 4;
    /**
     * 图片验证码干扰线数量，0表示不画干扰线
     */
    private int imageLineCount = 30;

    /**
     * 短信验证码有效期
     */
    private Duration smsExpire = Duration.ofMinutes(5);
    /**
     * 相同场景下同一接收目标（如手机号）的发送间隔
     */
    private Duration smsSendInterval = Duration.ofSeconds(60);
    /**
     * 短信验证码位数
     */
    private int smsCodeLength = 6;
    /**
     * 同一手机号每日最多发送条数，小于等于0表示不限制
     */
    private int smsMaxPerMobilePerDay = 20;
    /**
     * 允许的短信场景白名单，为空表示不限制（存在轮换场景绕过发送频率限制的风险）
     */
    private Set<String> smsScenes = new LinkedHashSet<>(Set.of(CaptchaScene.LOGIN, CaptchaScene.REGISTER,
            CaptchaScene.RESET_PASSWORD, CaptchaScene.BIND_MOBILE, CaptchaScene.CHANGE_MOBILE));
    /**
     * 验证码在短信模板参数中的占位名，
     * 配合{@code app.sms.templates}中配置的场景模板使用
     */
    private String smsCodeParam = "code";
    /**
     * 未配置短信模板时使用的文本短信内容模板，
     * {}依次为验证码、有效分钟数，需与服务商支持的内容报备格式保持一致
     */
    private String smsContentTemplate = "您的验证码是{}，{}分钟内有效，请勿泄露给他人。";

}
