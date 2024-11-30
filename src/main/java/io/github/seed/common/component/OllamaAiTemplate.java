package io.github.seed.common.component;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * 2024/11/27 ollama ai
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
public class OllamaAiTemplate implements AiTemplate {

    private final OllamaChatClient ollamaChatClient;

    @Override
    public Flux<String> chatStream(String message) {
        Flux<String> flux = ollamaChatClient.stream(message);
        return flux;
    }
}
