package com.zhangdp.seed.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 2023/4/12 事件处理器注解，拥有这个注解的类将会被自动扫描成spring bean
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface EventHandler {

    /**
     * 事件类型
     *
     * @return
     */
    String type();

    /**
     * 执行顺序
     *
     * @return
     */
    int order() default Integer.MAX_VALUE / 2;

}
