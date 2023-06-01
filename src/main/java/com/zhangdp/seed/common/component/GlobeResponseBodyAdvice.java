package com.zhangdp.seed.common.component;

import com.zhangdp.seed.common.R;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
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
@RestControllerAdvice
@Slf4j
public class GlobeResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 处理条件判断
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
        // 本身已经是R或者R的子类则不处理
        return !returnType.getParameterType().isAssignableFrom(R.class);
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
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        return R.success(null, body);
    }
}
