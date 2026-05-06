package io.github.seed.common.advice;

import io.github.seed.common.annotation.Desensitization;
import io.github.seed.common.annotation.RecordOperationLog;
import io.github.seed.common.constant.Const;
import io.github.seed.common.data.OperateEvent;
import io.github.seed.common.data.R;
import io.github.seed.entity.log.OperationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.json.JsonMapper;

/**
 * 记录接口操作日志Advice
 * 执行顺序：最后
 *
 * @author zhangdp
 * @since 2026/4/27
 */
@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@RequiredArgsConstructor
// 只包含自己写的controller，不能影响其他框架自带的controller
@RestControllerAdvice(basePackages = "io.github.seed")
public class RecordOperationLogResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final JsonMapper jsonMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 方法有@RecordOperationLog注解
        return returnType.hasMethodAnnotation(RecordOperationLog.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Object attr = request.getAttributes().get(Const.REQUEST_ATTR_OPERATION);
        if (attr instanceof OperateEvent event) {
            try {
                // 发出操作日志事件，从而有需要的话监听记录日志，就算接口抛异常了经过GlobalExceptionHandleAdvice全局异常处理后也会走到这里
                if (body != null) {
                    // 记录返回值
                    if (body instanceof String str) {
                        event.setResult(str);
                    } else {
                        event.setResult(jsonMapper.writeValueAsString(body));
                        // 如果是R类型，则可以记录返回状态码
                        if (body instanceof R<?> r) {
                            event.setResultCode(r.getCode());
                        }
                    }
                }
                // 如果没有返回值的情况或者返回值不是R，返回状态码只有成功、失败（抛异常）两种
                if (event.getResultCode() == null) {
                    event.setResultCode(event.getThrowable() == null ? Const.RESULT_SUCCESS : Const.RESULT_FAIL);
                }
                // 发出事件，异步记录
                applicationEventPublisher.publishEvent(event);
                log.debug("发出记录操作日志事件：{}", event);
            } catch(Exception e){
                log.error("发出操作日志事件失败，event={}", event, e);
            }
        }
        // 原样返回
        return body;
    }
}
