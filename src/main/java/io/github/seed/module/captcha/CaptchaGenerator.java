package io.github.seed.module.captcha;

import cn.hutool.v7.core.util.RandomUtil;
import cn.hutool.v7.swing.captcha.CaptchaUtil;
import cn.hutool.v7.swing.captcha.LineCaptcha;
import org.springframework.stereotype.Component;

/**
 * 验证码生成器
 * <br>图片验证码使用hutool-swing（{@code cn.hutool.v7.swing.captcha}包），
 * 服务端启动时会自动设置{@code java.awt.headless=true}，无需额外配置
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
public class CaptchaGenerator {

    /**
     * 验证码key长度
     */
    private static final int KEY_LENGTH = 32;

    /**
     * 生成图片验证码
     *
     * @param width      图片宽度
     * @param height     图片高度
     * @param codeLength 验证码字符个数
     * @param lineCount  干扰线数量
     * @return 验证码与图片
     */
    public GeneratedImage generateImage(int width, int height, int codeLength, int lineCount) {
        LineCaptcha captcha = CaptchaUtil.ofLineCaptcha(width, height, codeLength, Math.max(lineCount, 0));
        // getCode()内部会触发生成图片字节
        String code = captcha.getCode();
        return new GeneratedImage(code, captcha.getImageBase64Data());
    }

    /**
     * 生成短信验证码
     *
     * @param codeLength 验证码位数
     * @return 验证码
     */
    public String generateSmsCode(int codeLength) {
        return RandomUtil.randomNumbers(codeLength);
    }

    /**
     * 生成验证码key，只使用小写字母和数字，避免大小写歧义
     *
     * @return 验证码key
     */
    public String generateKey() {
        return RandomUtil.randomString(RandomUtil.LETTERS_NUMBERS_LOWER, KEY_LENGTH);
    }

    /**
     * 生成的图片验证码
     *
     * @param code  验证码字符
     * @param image 图片base64，带{@code data:image/png;base64,}前缀，可直接用于img标签的src
     */
    public record GeneratedImage(String code, String image) {

    }
}
