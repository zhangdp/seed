package io.github.seed.common.event;

import io.github.seed.common.constant.EventNameConst;
import io.github.seed.common.data.ServiceEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 2024/10/4
 *
 * @author zhangdp
 * @since
 */
@Component
@Slf4j
public class TestEventHandler {

    /**
     * 添加用户事件处理
     *
     * @param event
     */
    @Async
    @EventListener(condition = "#event.name == '" + EventNameConst.TEST + "'")
    @SneakyThrows
    public void test(ServiceEvent event) {
        log.debug("测试事件处理: {}", event);
        System.out.println(event.getName());
        System.out.println(event.getTag());
        System.out.println(event.getParams());
        System.out.println(event.getResult());
        System.out.println(event.getTimestamp());
        System.out.println(event.getSource());
        Thread.sleep(3000L);
        System.out.println(Thread.currentThread().getName());
    }

}
