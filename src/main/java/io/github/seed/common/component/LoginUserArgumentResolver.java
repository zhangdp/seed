package io.github.seed.common.component;

import io.github.seed.common.exception.UnauthorizedException;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.data.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 2027/7/14 自定义spring mvc controller方法参数注入器，自动注入当前登录用户
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        // LoginUser类型
        return methodParameter.getParameterType().equals(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new UnauthorizedException("无认证信息");
        }
        log.debug("自动注入当前登录用户: {}", loginUser);
        return loginUser;
    }
}
