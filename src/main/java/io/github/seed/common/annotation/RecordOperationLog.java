package io.github.seed.common.annotation;

import io.github.seed.common.enums.OperateType;

import java.lang.annotation.*;

/**
 * 记录操作日志注解，在controller方法生效
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RecordOperationLog {

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
    String description();

    /**
     * 关联模块
     *
     * @return
     */
    String refModule();

    /**
     * 关联模块id，spel表达式
     *
     * @return
     */
    String refIdEl() default "";

    /**
     * 如果出错是否记录
     *
     * @return
     */
    boolean recordIfError() default false;

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
    boolean logParameter() default true;

    /**
     * 是否记录请求头
     *
     * @return
     */
    boolean logHeader() default true;

    /**
     * 是否记录请求体
     *
     * @return
     */
    boolean logRequestBody() default true;

}
