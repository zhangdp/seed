package io.github.seed.module.captcha.component;

import cn.hutool.v7.core.util.RandomUtil;

/**
 * 验证码生成器：负责随机码与key的生成；行为验证码图片由tianai-captcha生成，不经过这里
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class CaptchaGenerator {

    /**
     * 验证码key长度
     */
    private static final int KEY_LENGTH = 32;

    /**
     * 生成数字验证码，用于短信验证码与行为验证码校验通过后签发的一次性票据
     *
     * @param length 验证码位数
     * @return 验证码
     */
    public String generateCode(int length) {
        return RandomUtil.randomNumbers(length);
    }

    /**
     * 生成验证码key，只使用小写字母和数字，避免大小写歧义
     *
     * @return 验证码key
     */
    public String generateKey() {
        return RandomUtil.randomString(RandomUtil.LETTERS_NUMBERS_LOWER, KEY_LENGTH);
    }
}
