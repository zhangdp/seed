package com.zhangdp.seed.common.annotation;

import com.zhangdp.seed.common.enums.OperateType;

import java.lang.annotation.*;

/**
 * 2023/4/17 记录操作日志注解，在controller方法失效
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface OperationLog {

    /**
     * 操作类型
     *
     * @return
     */
    OperateType type();

    /**
     * 操作描述
     *
     * @return
     */
    String title();

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

    /**
     * 是否记录结果
     *
     * @return
     */
    boolean logResult() default true;

    /**
     * 是否记录入参
     *
     * @return
     */
    boolean logParams() default true;

}
