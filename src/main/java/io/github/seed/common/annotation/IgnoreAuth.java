package io.github.seed.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略认证，使用在controller，如果加在类上面则代表该controller所有接口均不需要认证；加在方法上代表该接口不需要认证
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IgnoreAuth {
}
