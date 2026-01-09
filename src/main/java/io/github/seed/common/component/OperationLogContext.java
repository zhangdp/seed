package io.github.seed.common.component;

import io.github.seed.common.data.OperateEvent;
import jakarta.validation.constraints.NotNull;

/**
 * 操作日志上下文
 *
 * @author zhangdp
 * @since 2026/1/9
 */
public class OperationLogContext {

    private static final ThreadLocal<OperateEvent> threadLocal = new ThreadLocal<>();

    public static void set(@NotNull OperateEvent event) {
        threadLocal.set(event);
    }

    public static OperateEvent get() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }
}
