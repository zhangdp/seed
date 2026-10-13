package io.github.seed.module.captcha.store;

import io.github.seed.module.captcha.data.CaptchaType;

import java.time.Duration;

/**
 * 验证码存储：只存验证码本身与其配套的失败次数、发送锁
 * <br>不掺任何短信业务规则——「某手机号当天能发几条」是发送通道的配额，不在本接口的职责内
 * <br>实现类可以是redis、本地缓存或数据库；key怎么拼是实现细节，因此接口不暴露key
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface CaptchaStore {

    /**
     * 保存验证码
     *
     * @param type   验证码类型
     * @param scene  场景
     * @param key    验证码key
     * @param code   验证码
     * @param expire 过期时间
     */
    void saveCode(CaptchaType type, String scene, String key, String code, Duration expire);

    /**
     * 获取验证码
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     * @return 验证码，不存在时返回null
     */
    String getCode(CaptchaType type, String scene, String key);

    /**
     * 删除验证码
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     */
    void deleteCode(CaptchaType type, String scene, String key);

    /**
     * 获取验证码剩余有效期，用于给失败次数续同样的时间
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     * @return 剩余过期时间，不存在或已过期时返回0
     */
    Duration getCodeExpire(CaptchaType type, String scene, String key);

    /**
     * 累加校验失败次数
     *
     * @param type   验证码类型
     * @param scene  场景
     * @param key    验证码key
     * @param expire 过期时间，用于避免产生永不失效的残留key
     * @return 累加后的失败次数
     */
    long increaseFail(CaptchaType type, String scene, String key, Duration expire);

    /**
     * 删除校验失败次数
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     */
    void deleteFail(CaptchaType type, String scene, String key);

    /**
     * 抢占式设置发送锁
     *
     * @param type     验证码类型
     * @param scene    场景
     * @param key      验证码key
     * @param interval 发送间隔
     * @return true表示抢占成功可以发送
     */
    boolean tryLockSend(CaptchaType type, String scene, String key, Duration interval);

    /**
     * 释放发送锁
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     */
    void releaseSendLock(CaptchaType type, String scene, String key);

    /**
     * 获取发送锁剩余有效期，用于提示「多少秒后可再发」
     *
     * @param type  验证码类型
     * @param scene 场景
     * @param key   验证码key
     * @return 剩余过期时间，不存在或已过期时返回0
     */
    Duration getSendLockExpire(CaptchaType type, String scene, String key);

}
