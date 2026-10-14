package io.github.seed.module.captcha;

import io.github.seed.module.captcha.data.CaptchaScene;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 验证码配置（{@code app.captcha}），按验证码类型分成{@code image}与{@code sms}两段
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
     * 图片验证码配置（{@code app.captcha.image}）
     */
    private Image image = new Image();
    /**
     * 短信验证码配置（{@code app.captcha.sms}）；发送侧规则（间隔、配额）不在这里，归{@code app.sms}
     */
    private Sms sms = new Sms();

    /**
     * 图片验证码配置
     */
    @Getter
    @Setter
    public static class Image {

        /**
         * 验证码有效期
         */
        private Duration expire = Duration.ofMinutes(5);
        /**
         * 默认宽度
         */
        private int width = 200;
        /**
         * 默认高度
         */
        private int height = 50;
        /**
         * 允许的最小宽度，防止被构造超大图片
         */
        private int minWidth = 80;
        /**
         * 允许的最大宽度，防止被构造超大图片
         */
        private int maxWidth = 400;
        /**
         * 允许的最小高度，防止被构造超大图片
         */
        private int minHeight = 30;
        /**
         * 允许的最大高度，防止被构造超大图片
         */
        private int maxHeight = 200;
        /**
         * 验证码字符个数
         */
        private int codeLength = 4;
        /**
         * 干扰线数量，0表示不画干扰线
         */
        private int lineCount = 30;
    }

    /**
     * 短信验证码配置
     */
    @Getter
    @Setter
    public static class Sms {

        /**
         * 验证码有效期
         */
        private Duration expire = Duration.ofMinutes(5);
        /**
         * 验证码位数
         */
        private int codeLength = 6;
        /**
         * 允许的场景白名单，为空表示不限制（存在轮换场景绕过发送频率限制的风险）
         */
        private Set<String> scenes = new LinkedHashSet<>(Set.of(CaptchaScene.LOGIN, CaptchaScene.REGISTER,
                CaptchaScene.RESET_PASSWORD, CaptchaScene.BIND_MOBILE, CaptchaScene.CHANGE_MOBILE));
        /**
         * 验证码在短信模板参数中的占位名，
         * 配合{@code app.sms.templates}中配置的场景模板使用
         */
        private String codeParam = "code";
        /**
         * 未配置短信模板时使用的文本短信内容模板，
         * {}依次为验证码、有效分钟数，需与服务商支持的内容报备格式保持一致
         */
        private String contentTemplate = "您的验证码是{}，{}分钟内有效，请勿泄露给他人。";
    }

}
