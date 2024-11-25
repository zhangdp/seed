package io.github.seed.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * 2024/11/25
 *
 * @author zhangdp
 * @since
 */
@Slf4j
@SpringBootTest
public class OllamaTest {

    @Autowired
    private OllamaChatClient ollamaChatClient;

    @Test
    public void chat() {
        String req = "你好";
        log.debug("发送消息给ollama模型：{}", req);
        String res = ollamaChatClient.call(req);
        log.debug("ollama模型回答：{}", res);
    }

    @Test
    public void chatStream() {
        String req = "你好";
        log.debug("发送消息给ollama模型：{}", req);
        Flux<String> flux = ollamaChatClient.stream(req);
        flux.doOnEach(o -> log.debug("ollama模型流式回答：{}", o));
    }
}
