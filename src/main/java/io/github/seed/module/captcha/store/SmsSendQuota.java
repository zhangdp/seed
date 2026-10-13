package io.github.seed.module.captcha.store;

/**
 * 短信验证码的发送配额：按手机号记当日已发条数
 * <br>与{@link CaptchaStore}分开的意义：验证码存哪是一回事，短信通道愿意为一个手机号发几条
 * 是另一回事，两者可以各自换实现（比如配额改从sys_sms_log统计）
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface SmsSendQuota {

    /**
     * 累加手机号当日发送次数，跨过零点自动从1重新计数
     *
     * @param mobile 手机号
     * @return 累加后的当日发送次数
     */
    long increaseDaily(String mobile);

}
