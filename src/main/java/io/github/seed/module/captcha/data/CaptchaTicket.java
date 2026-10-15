package io.github.seed.module.captcha.data;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 行为验证码校验通过后签发的一次性票据：
 * 业务接口（如登录）提交{@code key}与{@code code}，由{@code @VerifyCaptcha}切面校验并消费
 *
 * @param key  票据标识
 * @param code 票据码
 * @author zhangdp
 * @since 1.0.0
 */
@Schema(title = "验证码票据")
public record CaptchaTicket(

        @Schema(title = "票据标识", description = "提交给业务接口的captchaKey") String key,

        @Schema(title = "票据码", description = "提交给业务接口的code") String code) {

}
