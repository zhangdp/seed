package io.github.seed.common.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.common.data.R;
import io.github.seed.common.annotation.NoResponseAdvice;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;

/**
 * 2023/5/31 全局统一返回
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
// 只包含自己写的controller，不能影响其他框架自带的controller
@RestControllerAdvice(basePackages = "io.github.seed")
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    /**
     * 处理条件判断
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 不是R或者R的子类
        if (R.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }
        // 所在方法没有@NoResponseAdvice注解
        if (returnType.hasMethodAnnotation(NoResponseAdvice.class)) {
            return false;
        }
        // 所在类没有@NoResponseAdvice注解
        if (returnType.getDeclaringClass().isAnnotationPresent(NoResponseAdvice.class)) {
            return false;
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
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 已经显示调用response.getWriter()输出了的不再包装
        if (response instanceof ServletServerHttpResponse servletResponse
                && servletResponse.getServletResponse().isCommitted()) {
            return body; // 不包装
        }

        if (body instanceof ResponseEntity<?> entity) {
            Object entityBody = entity.getBody();
            // 流 / 资源：直接返回
            if (entityBody instanceof Resource ||
                    entityBody instanceof StreamingResponseBody ||
                    entityBody instanceof InputStream ||
                    entityBody instanceof byte[]) {
                return entity;
            }
            log.debug("使用{}统一包装ResponseEntity的返回：{}", R.class, entityBody != null ? entityBody.getClass() : null);
            // 非流：重新包装 body
            return ResponseEntity
                    .status(entity.getStatusCode())
                    .headers(entity.getHeaders())
                    .body(R.success("", entityBody));
        }

        // 流 / 资源：直接返回
        if (body instanceof Resource ||
                body instanceof StreamingResponseBody ||
                body instanceof InputStream ||
                body instanceof byte[]) {
            return body;
        }

        R<?> r = R.success("", body);
        // String类型spring是直接返回，因此R包装后需手动转为json字符串返回，不然会报错
        if (body instanceof String) {
            HttpHeaders headers = response.getHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return objectMapper.writeValueAsString(r);
        }
        log.debug("使用{}统一包装返回：{}", R.class, body != null ? body.getClass() : null);
        return r;
    }
}
