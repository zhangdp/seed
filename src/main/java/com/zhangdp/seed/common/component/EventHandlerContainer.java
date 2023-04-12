package com.zhangdp.seed.common.component;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 2023/4/12 事件处理器容器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
public class EventHandlerContainer {

    /**
     * 处理器链，按照order升序
     */
    private final Map<String, List<EventHandlerWrapper>> handlerMap = new HashMap<>();

    /**
     * 添加处理器
     *
     * @param type
     * @param handler
     * @param order
     */
    public void addHandler(@NotBlank String type, @NotNull IEventHandler handler, int order) {
        Assert.notEmpty(type, "事件类型为空");
        Assert.notNull(handler, "事件处理器为null");
        synchronized (handlerMap) {
            List<EventHandlerWrapper> list = handlerMap.computeIfAbsent(type, k -> new ArrayList<>());
            list.add(new EventHandlerWrapper(order, handler));
            log.info("添加事件处理：type={}, handler={}, order={}", type, handler.getClass().getName(), order);
            list.sort(Comparator.comparingInt(EventHandlerWrapper::getOrder));
        }
    }

    /**
     * 获取某个类型的处理器列表
     *
     * @param type
     * @return
     */
    public List<IEventHandler> getHandlers(String type) {
        List<EventHandlerWrapper> list = handlerMap.get(type);
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        List<IEventHandler> ret = new ArrayList<>(list.size());
        for (EventHandlerWrapper eventHandlerWrapper : list) {
            ret.add(eventHandlerWrapper.getEventHandler());
        }
        return ret;
    }

    /**
     * 是否存在指定类型
     *
     * @param type
     * @return
     */
    public boolean hasHandlers(String type) {
        return handlerMap.containsKey(type);
    }

    @Data
    @AllArgsConstructor
    private static class EventHandlerWrapper {
        private int order;
        private IEventHandler eventHandler;
    }

}
