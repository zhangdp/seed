package io.github.seed.common.annotation;

import java.lang.annotation.*;

/**
 * 2023/4/12 事件注解
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Event {

    /**
     * 事件类型
     *
     * @return
     */
    String type();

    /**
     * 是否执行条件，空表示无需判断即执行，spel表达式
     *
     * @return
     */
    String condition() default "";

    /**
     * 是否异步触发，默认是
     * 采用spring @Async方式，需要在启动类添加注解@EnableAsync才会生效
     *
     * @return
     */
    boolean isAsync() default true;

    /**
     * 延迟执行，单位毫秒，默认0不延迟
     *
     * @return
     */
    int delay() default 0;

    /**
     * 额外标签
     *
     * @return
     */
    String tag() default "";

}
