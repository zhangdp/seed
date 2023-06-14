package com.zhangdp.seed.common.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.R;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 2023/5/31 全局统一返回
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
// 只包含自己写的controller，不能影响其他框架自带的controller
@RestControllerAdvice(basePackages = "com.zhangdp.seed.controller")
public class GlobeResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 忽略转换的返回类型
     */
    private final static Class<?>[] IGNORE_CLASS = {R.class};
    /**
     * jsckson
     */
    private final ObjectMapper objectMapper;

    /**
     * 处理条件判断
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 忽略的类型则不处理
        for (Class<?> clazz : IGNORE_CLASS) {
            if (returnType.getParameterType().isAssignableFrom(clazz)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 统一用R包装返回
     *
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     * @see R
     */
    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NotNull ServerHttpRequest request,
                                  @NotNull ServerHttpResponse response) {
        // String类型spring是直接返回，因此R包装后需手动转为json字符串返回，不然会报错
        if (body instanceof String) {
            HttpHeaders headers = response.getHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return objectMapper.writeValueAsString(R.success(null, body));
        }
        return R.success(null, body);
    }
}
