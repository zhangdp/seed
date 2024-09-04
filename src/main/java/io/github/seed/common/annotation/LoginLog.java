package io.github.seed.common.annotation;

import java.lang.annotation.*;

/**
 * 2023/8/4 记录登录日志注解
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface LoginLog {
}
