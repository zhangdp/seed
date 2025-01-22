package io.github.seed.common.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.common.data.R;
import io.github.seed.common.annotation.NoResponseAdvice;
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
@RestControllerAdvice(basePackages = "io.github.seed.controller")
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * jackson
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
        return
                // controller类没有@NoResponseAdvice注解
                !returnType.getDeclaringClass().isAnnotationPresent(NoResponseAdvice.class)
                // 方法没有@NoResponseAdvice注解
                && !returnType.hasMethodAnnotation(NoResponseAdvice.class)
                // 不是R或者R的子类
                && !R.class.isAssignableFrom(returnType.getParameterType());
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
