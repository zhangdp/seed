package io.github.seed.common.advice;

import io.github.seed.common.annotation.NoWrapperResponse;
import io.github.seed.common.data.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
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
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;

/**
 * 全局统一使用R包装返回
 * 执行顺序：2
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Order(2)
@RequiredArgsConstructor
// 只包含自己写的controller，不能影响其他框架自带的controller
@RestControllerAdvice(basePackages = "io.github.seed")
public class WrapperResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final JsonMapper jsonMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 本来就是R或者R的子类无需再包装
        if (R.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }
        // 方法有@NoResponseAdvice注解表示无需包装
        if (returnType.hasMethodAnnotation(NoWrapperResponse.class)) {
            return false;
        }
        // 所在类有@NoResponseAdvice注解表示无需包装
        if (returnType.getDeclaringClass().isAnnotationPresent(NoWrapperResponse.class)) {
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
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        R<?> result = null;

        // 已经显示调用response.getWriter()输出了的不再包装
        if (response instanceof ServletServerHttpResponse servletResponse
                && servletResponse.getServletResponse().isCommitted()) {
            return body;
        }

        // 流 / 资源：直接返回
        if (body instanceof Resource ||
                body instanceof StreamingResponseBody ||
                body instanceof InputStream ||
                body instanceof byte[]) {
            return body;
        }

        if (body instanceof ResponseEntity<?> entity) {
            Object entityBody = entity.getBody();
            // 流 / 资源：直接返回
            if (entityBody instanceof Resource ||
                    entityBody instanceof StreamingResponseBody ||
                    entityBody instanceof InputStream ||
                    entityBody instanceof byte[]) {
                return body;
            }
            log.debug("使用{}统一包装ResponseEntity的返回：{}", R.class, entityBody != null ? entityBody.getClass() : null);
            // 非流：重新包装 body
            result = R.success("", entityBody);
            return ResponseEntity
                    .status(entity.getStatusCode())
                    .headers(entity.getHeaders())
                    .body(result);
        }

        result = R.success("", body);
        // String类型spring是直接返回，因此R包装后需手动转为json字符串返回，不然会报错
        if (body instanceof String) {
            HttpHeaders headers = response.getHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            log.debug("使用{}统一包装String返回", R.class);
            return jsonMapper.writeValueAsString(result);
        }
        log.debug("使用{}统一包装返回：{}", R.class, body != null ? body.getClass() : null);

        return result;
    }
}
