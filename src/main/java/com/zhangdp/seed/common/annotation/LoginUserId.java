package com.zhangdp.seed.common.annotation;

import io.swagger.v3.oas.annotations.Hidden;

import java.lang.annotation.*;

/**
 * 2023/6/14 spring mvc controller自动注入当前登录用户注解
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Documented
@Hidden
public @interface LoginUserId {

}
