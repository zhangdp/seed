package io.github.seed.common.event;

import io.github.seed.common.constant.EventNameConst;
import io.github.seed.common.data.ServiceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 2024/10/4 新增用户事件处理
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@Slf4j
public class AddUserEventHandler {

    /**
     * 添加用户事件处理
     *
     * @param event
     */
    @Async
    @EventListener(condition = "#event.name == '" + EventNameConst.ADD_USER + "'")
    public void addUserEvent(ServiceEvent event) {
        log.debug("Add user event: {}", event);
    }

}
