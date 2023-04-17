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
public @interface OpearteLog {

    /**
     * 操作类型
     *
     * @return
     */
    OperateType type();
}
