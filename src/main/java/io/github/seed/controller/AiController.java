package io.github.seed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * 2024/11/25
 *
 * @author zhangdp
 * @since
 */
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final OllamaChatClient ollamaChatClient;

    @PostMapping("/chat/stream")
    public SseEmitter chatStream(String message) {
        SseEmitter sse = new SseEmitter(60000L);
        Flux<String> flux = ollamaChatClient.stream(message);
        flux.subscribe(item -> {
            try {
                sse.send(item);
            } catch (IOException e) {
                sse.completeWithError(e);
            }
        }, error -> {
            sse.completeWithError(error);
        }, () -> {
            sse.complete();
        });
        return sse;
    }
}
