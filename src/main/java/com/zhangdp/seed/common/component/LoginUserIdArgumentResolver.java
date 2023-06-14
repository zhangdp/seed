package com.zhangdp.seed.common.component;

import cn.dev33.satoken.stp.StpUtil;
import com.zhangdp.seed.common.annotation.LoginUserId;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 2023/6/14 spring mvc controller方法参数注入带有@LoginUserId的当前登录用户id
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class LoginUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // 必须拥有@LoginUser注解
        return methodParameter.hasParameterAnnotation(LoginUserId.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  @NotNull NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        Long result = StpUtil.getLoginIdAsLong();
        if (log.isDebugEnabled()) {
            log.debug("注入当前登录用户id：{}", result);
        }
        return result;
    }
}
