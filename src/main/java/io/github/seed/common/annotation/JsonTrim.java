package io.github.seed.common.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import io.github.seed.common.component.StringTrimDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2025/3/8 String类型自动trim()注解
 * 已全局配置生效，无需使用此注解
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Deprecated
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = StringTrimDeserializer.class)
public @interface JsonTrim {
}
