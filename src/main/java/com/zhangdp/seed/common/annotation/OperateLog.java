package com.zhangdp.seed.common.annotation;

import com.zhangdp.seed.common.enums.OperateType;

import java.lang.annotation.*;

/**
 * 2023/4/17 记录操作日志注解
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OperateLog {

    /**
     * 操作类型
     *
     * @return
     */
    OperateType type();

    /**
     * 关联模块
     *
     * @return
     */
    String refModule() default "";

    /**
     * 关联模块id，spel表达式
     *
     * @return
     */
    String refIdEl() default "";

    /**
     * 忽略的参数名称
     *
     * @return
     */
    String[] ignoreParams() default {};

    /**
     * 如果出错是否记录
     *
     * @return
     */
    boolean logIfError() default false;


}
