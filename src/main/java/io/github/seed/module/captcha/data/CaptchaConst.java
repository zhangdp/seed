package io.github.seed.module.captcha.data;

/**
 * 验证码相关常量
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface CaptchaConst {

    /**
     * redis key 前缀
     */
    String REDIS_PREFIX = "captcha";
    /**
     * redis key 分隔符
     */
    String REDIS_SPLIT = "::";

    /**
     * 验证码key前缀，完整格式：captcha::{type}::{scene}::{key}
     */
    String REDIS_CODE_PREFIX = REDIS_PREFIX;
    /**
     * 校验失败次数key前缀，完整格式：captcha::fail::{type}::{scene}::{key}
     */
    String REDIS_FAIL_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "fail";
    /**
     * 发送频率限制key前缀，完整格式：captcha::limit::{type}::{scene}::{key}
     */
    String REDIS_LIMIT_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "limit";
    /**
     * 短信每日发送次数key前缀，完整格式：captcha::count::sms::daily::{mobile}
     */
    String REDIS_SMS_DAILY_PREFIX = REDIS_PREFIX + REDIS_SPLIT + "count" + REDIS_SPLIT + CaptchaType.SMS.type()
            + REDIS_SPLIT + "daily";
}
