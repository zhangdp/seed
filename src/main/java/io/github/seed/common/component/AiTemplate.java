package io.github.seed.common.component;

import reactor.core.publisher.Flux;

/**
 * 2024/11/27 ai
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface AiTemplate {

    /**
     * 流式对话
     *
     * @param message
     * @return
     */
    Flux<String> chatStream(String message);
}
