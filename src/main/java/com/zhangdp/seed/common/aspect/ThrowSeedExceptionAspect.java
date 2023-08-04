package com.zhangdp.seed.common.aspect;

import com.zhangdp.seed.common.annotation.ThrowSeedException;
import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.SeedException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.stereotype.Component;

/**
 * 2023/8/3 带有@ThrowSeedException注解的方法抛异常后转为自定义异常SeedException抛出
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
     * @param throwable
     * @param throwSeedException
     */
    @AfterThrowing(throwing = "throwable", value = "@annotation(throwSeedException)")
    public void afterThrowing(JoinPoint joinPoint, Throwable throwable, ThrowSeedException throwSeedException) {
        if (log.isDebugEnabled()) {
            String method = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
            log.debug("SeedExceptionAspect afterThrowing: method={}, throwable={}", method, throwable.getClass().getName());
        }
        ErrorCode errorCode = throwSeedException.value();
        throw new SeedException(errorCode.code(), StrUtil.defaultIfEmpty(throwSeedException.message(), errorCode.message()), throwable);
    }
}
