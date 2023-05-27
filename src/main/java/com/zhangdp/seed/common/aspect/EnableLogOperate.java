package com.zhangdp.seed.common.aspect;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 2023/5/27 开启记录操作日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(OperateLogAspect.class)
public @interface EnableLogOperate {
}
