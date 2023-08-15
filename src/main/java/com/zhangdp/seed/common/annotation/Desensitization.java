package com.zhangdp.seed.common.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zhangdp.seed.common.component.DesensitizationSerializer;
import com.zhangdp.seed.common.enums.DesensitizationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2023/8/14 脱敏注解类。在需要脱敏的字段上添加此注解，返回前端json就会自动脱敏。只对spring jackson生效
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizationSerializer.class)
public @interface Desensitization {

    /**
     * 脱敏数据类型。只有在CUSTOMER的时候，start和end生效
     *
     * @return
     */
    DesensitizationType value();

    /**
     * 脱敏开始位置（包含）
     *
     * @return
     */
    int start() default -1;

    /**
     * 脱敏结束位置（不包含）
     *
     * @return
     */
    int end() default -1;
    /**
     * 替换字符
     * @return
     */
    // char mask() default '*';
}
