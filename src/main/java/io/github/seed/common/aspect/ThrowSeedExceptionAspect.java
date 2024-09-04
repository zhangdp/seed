package io.github.seed.common.aspect;

import io.github.seed.common.annotation.ThrowSeedException;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.stereotype.Component;

/**
 * 2023/8/3 带有@ThrowSeedException注解的方法抛异常后转为自定义异常BizException抛出
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Aspect
@Component
@Slf4j
public class ThrowSeedExceptionAspect {

    /**
     * 带有@ThrowSeedException注解的方法抛异常后转为自定义异常SeedException抛出
     *
     * @param joinPoint
     * @param throwing
     * @param throwSeedException
     */
    @AfterThrowing(throwing = "throwing", value = "@annotation(throwSeedException)")
    public void afterThrowing(JoinPoint joinPoint, Throwable throwing, ThrowSeedException throwSeedException) {
        if (log.isDebugEnabled()) {
            String method = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
            log.debug("SeedExceptionAspect afterThrowing: method={}, throwing={}", method, throwing.getClass().getName());
        }
        BizException bizException;
        if (throwing instanceof BizException se) {
            bizException = se;
        } else {
            ErrorCode errorCode = throwSeedException.value();
            bizException = new BizException(errorCode.code(), StrUtil.defaultIfEmpty(throwSeedException.message(), errorCode.message()), throwing);
        }
        throw bizException;
    }
}
