package com.zhangdp.seed.event.component;

import com.zhangdp.seed.event.annotation.EventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 2023/4/12 自动扫描注册带有@EventHandler注解的处理器类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventHandlerRegister implements ApplicationContextAware, SmartInitializingSingleton {

    /**
     * 事件处理器容器
     */
    private final EventHandlerContainer eventHandlerContainer;
    /**
     * spring环境
     */
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = this.applicationContext.getBeansWithAnnotation(EventHandler.class);
        if (!beans.isEmpty()) {
            beans.forEach(this::registerHandler);
        }
    }

    /**
     * 注册处理器
     *
     * @param name
     * @param bean
     */
    private void registerHandler(String name, Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        if (!(bean instanceof IEventHandler)) {
            throw new IllegalStateException(clazz + " 不属于 " + IEventHandler.class.getName());
        }
        EventHandler annotation = clazz.getAnnotation(EventHandler.class);
        String type = annotation.type();
        int order = annotation.order();
        eventHandlerContainer.addHandler(type, (IEventHandler) bean, order);
    }

}
