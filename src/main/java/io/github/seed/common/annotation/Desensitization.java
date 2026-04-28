package io.github.seed.common.annotation;

import java.lang.annotation.*;

/**
 * 脱敏注解类，加在controller方法或者类上，需要配合具体Sensitive表明敏感数据类型
 *
 * @author zhangdp
 * @since 2026/4/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface Desensitization {
}
