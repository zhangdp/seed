package io.github.seed.module.captcha.store;

import io.github.seed.module.captcha.data.CaptchaConst;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 短信发送配额的redis实现：计数到当日24点过期，因此无需额外的跨天清理
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class RedisSmsSendQuota implements SmsSendQuota {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public long increaseDaily(String mobile) {
        String dailyKey = CaptchaConst.REDIS_SMS_DAILY_PREFIX + CaptchaConst.REDIS_SPLIT + mobile;
        Long count = this.stringRedisTemplate.opsForValue().increment(dailyKey);
        long value = count == null ? 1L : count;
        if (value == 1L) {
            Duration ttl = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay());
            if (!ttl.isNegative() && !ttl.isZero()) {
                this.stringRedisTemplate.expire(dailyKey, ttl);
            }
        }
        log.debug("累加手机号当日验证码发送次数：key={}, count={}", dailyKey, value);
        return value;
    }

}
