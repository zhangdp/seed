package io.github.seed.common.component;

/**
 * 2023/4/12 事件处理器接口类
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface IEventHandler {

    /**
     * 处理事件
     *
     * @param context
     */
    void handler(EventContext context);

    /**
     * 是否执行
     *
     * @return
     */
    default boolean enable() {
        return true;
    }

}
