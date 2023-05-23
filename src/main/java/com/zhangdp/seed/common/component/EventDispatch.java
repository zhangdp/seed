package com.zhangdp.seed.common.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.collection.CollUtil;
import org.dromara.hutool.core.thread.ThreadUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 2023/4/12 事件调度器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EventDispatch {

    /**
     * 事件处理器容器
     */
    private final EventHandlerContainer eventHandlerContainer;

    /**
     * 异步调度事件
     *
     * @param type
     * @param tag
     * @param params
     * @param delay
     * @param tag
     */
    @Async
    public void dispatchAsync(String type, String tag, Map<String, Object> params, Object result, int delay) {
        this.dispatch(type, tag, params, result, delay);
    }

    /**
     * 调度事件
     *
     * @param type
     * @param tag
     * @param params
     * @param delay
     * @param tag
     */
    public void dispatch(String type, String tag, Map<String, Object> params, Object result, int delay) {
        // todo 延迟执行，暂时用线程睡眠的方式，需换为更加合理的方案
        if (delay > 0) {
            ThreadUtil.sleep(delay);
        }
        List<IEventHandler> handlers = eventHandlerContainer.getHandlers(type);
        if (CollUtil.isEmpty(handlers)) {
            log.warn("未知类型事件：" + type);
        } else {
            for (IEventHandler handler : handlers) {
                try {
                    if (handler.enable()) {
                        handler.handler(params, result, tag);
                        if (log.isDebugEnabled()) {
                            log.debug("处理事件：event={}, handler={}", type, handler.getClass().getName());
                        }
                    }
                } catch (Exception e) {
                    log.error(handler.getClass().getName() + "处理出错", e);
                }
            }
        }
    }

}
