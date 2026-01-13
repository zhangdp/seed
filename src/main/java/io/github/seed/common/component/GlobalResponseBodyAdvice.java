package io.github.seed.common.component;

import io.github.seed.common.constant.Const;
import io.github.seed.common.data.OperateEvent;
import io.github.seed.common.data.R;
import io.github.seed.common.annotation.NoResponseAdvice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;

/**
 * 全局统一返回
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
// 只包含自己写的controller，不能影响其他框架自带的controller
@RestControllerAdvice(basePackages = "io.github.seed")
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final JsonMapper jsonMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 需要记录日志，因此全部返回true，具体由转换的时候再行判断需不需要
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
        Object result = body;
        boolean recordResult = true;
        try {
            // 本来就是R或者R的子类无需再包装
            if (R.class.isAssignableFrom(returnType.getParameterType())) {
                return body;
            }
            // 方法有@NoResponseAdvice注解表示无需包装
            if (returnType.hasMethodAnnotation(NoResponseAdvice.class)) {
                return body;
            }
            // 所在类有@NoResponseAdvice注解表示无需包装
            if (returnType.getDeclaringClass().isAnnotationPresent(NoResponseAdvice.class)) {
                return body;
            }

            // 已经显示调用response.getWriter()输出了的不再包装
            if (response instanceof ServletServerHttpResponse servletResponse
                    && servletResponse.getServletResponse().isCommitted()) {
                recordResult = false;
                return body;
            }

            // 流 / 资源：直接返回
            if (body instanceof Resource ||
                    body instanceof StreamingResponseBody ||
                    body instanceof InputStream ||
                    body instanceof byte[]) {
                recordResult = false;
                return body;
            }

            if (body instanceof ResponseEntity<?> entity) {
                Object entityBody = entity.getBody();
                // 流 / 资源：直接返回
                if (entityBody instanceof Resource ||
                        entityBody instanceof StreamingResponseBody ||
                        entityBody instanceof InputStream ||
                        entityBody instanceof byte[]) {
                    recordResult = false;
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
        } finally {
            // 发出操作日志事件，从而有需要的话监听记录日志，就算接口抛异常了经过GlobalExceptionHandleAdvice全局异常处理后也会走到这里
            this.publishEvent(result, recordResult);
        }
    }

    /**
     * 发出操作日志事件
     *
     * @param result
     * @param recordResult
     */
    private void publishEvent(Object result, boolean recordResult) {
        OperateEvent event = null;
        try {
            // 从上下文取出操作日志事件
            event = OperationLogContext.get();
            if (event != null) {
                if (result != null) {
                    // 记录返回值
                    if (recordResult) {
                        if (result instanceof String str) {
                            event.setResult(str);
                        } else {
                            event.setResult(jsonMapper.writeValueAsString(result));
                        }
                    }
                    // 如果是R类型，则可以记录返回状态码
                    if (result instanceof R<?> r) {
                        event.setResultCode(r.getCode());
                    }
                }
                // 如果没有返回值的情况或者返回值不是R，返回状态码只有成功、失败（抛异常）两种
                if (event.getResultCode() == null) {
                    event.setResultCode(event.getThrowable() == null ? Const.RESULT_SUCCESS : Const.RESULT_FAIL);
                }
                // 发出事件，异步记录
                applicationEventPublisher.publishEvent(event);
                log.debug("发布操作日志事件：{}", event);
            }
        } catch (Exception e) {
            log.error("发布操作日志事件失败：{}", event, e);
        } finally {
            // 清理上下文
            OperationLogContext.remove();
        }
    }
}
