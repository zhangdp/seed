package io.github.seed.common.annotation;

import java.lang.annotation.*;

/**
 * 2023/4/12 发布事件注解
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface PublishEvent {

    /**
     * 事件名称
     *
     * @return
     */
    String value();

    /**
     * 是否执行条件，空表示无需判断即执行，spel表达式
     *
     * @return
     */
    String condition() default "";

    /**
     * 额外标签
     *
     * @return
     */
    String tag() default "";

}
