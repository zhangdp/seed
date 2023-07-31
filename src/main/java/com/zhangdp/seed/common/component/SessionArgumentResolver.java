package com.zhangdp.seed.common.component;

import cn.dev33.satoken.stp.StpUtil;
import com.zhangdp.seed.common.data.Session;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 2023/6/14 spring mvc controller方法参数注入satoken session
 * <br>需保证已登录的上下文环境中
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class SessionArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(Session.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NotNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        Session session = new Session(StpUtil.getTokenSession());
        if (log.isDebugEnabled()) {
            log.debug("注入session：{}", session.getId());
        }
        return session;
    }
}
