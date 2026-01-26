package io.github.seed.common.component;

import io.github.seed.common.annotation.IgnoreAuth;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 忽略认证的接口路径注册器
 *
 * @author zhangdp
 * @since 1.0.0
 * @see io.github.seed.common.annotation.IgnoreAuth
 */
@Slf4j
public class IgnoreAuthPathRegistry implements InitializingBean {

    private final RequestMappingHandlerMapping handlerMapping;
    @Getter
    private final Set<String> ignoreAuthPaths;

    public IgnoreAuthPathRegistry(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
        ignoreAuthPaths = new LinkedHashSet<>();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        handlerMapping.getHandlerMethods().forEach((mapping, method) -> {
            if (method.hasMethodAnnotation(IgnoreAuth.class) || method.getBeanType().isAnnotationPresent(IgnoreAuth.class)) {
                Set<String> values = mapping.getPatternValues();
                this.ignoreAuthPaths.addAll(values);
                log.debug("忽略认证的接口，method={}，url={}", method, values);
            }
        });
    }

}
