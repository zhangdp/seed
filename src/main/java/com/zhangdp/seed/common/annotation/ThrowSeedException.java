package com.zhangdp.seed.common.annotation;

import com.zhangdp.seed.common.enums.ErrorCode;

import java.lang.annotation.*;

/**
 * 2023/8/3 如果出现异常，则包装成业务异常SeedException抛出
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ThrowSeedException {

    /**
     * 错误码枚举
     *
     * @return
     */
    ErrorCode value();

    /**
     * 自定义错误描述，如果为空则默认使用状态码枚举的错误描述
     *
     * @return
     */
    String message() default "";
}
