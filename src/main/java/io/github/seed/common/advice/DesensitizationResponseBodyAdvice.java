package io.github.seed.common.advice;

import io.github.seed.common.annotation.Desensitization;
import io.github.seed.common.util.DesensitizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 对controller返回值进行脱敏
 * 需要controller类或者方法带有@Desensitization注解
 * 执行顺序：1
 *
 * @author zhangdp
 * @since 2026/4/27
 */
@Slf4j
@Order(1)
@RequiredArgsConstructor
// 只包含自己写的controller，不能影响其他框架自带的controller
@RestControllerAdvice(basePackages = "io.github.seed")
public class DesensitizationResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 方法有@Desensitization注解或者所在类有@Desensitization注解
        return returnType.hasMethodAnnotation(Desensitization.class) ||
                returnType.getDeclaringClass().isAnnotationPresent(Desensitization.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 脱敏，直接修改原值
        DesensitizationUtil.desensitize(body);
        return body;
    }
}
