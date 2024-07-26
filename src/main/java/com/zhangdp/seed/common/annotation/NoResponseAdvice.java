package com.zhangdp.seed.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2024/7/27 接口返回不统一使用R包装注解，在需要的Controller类或者方法上使用此注解
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface NoResponseAdvice {
}
