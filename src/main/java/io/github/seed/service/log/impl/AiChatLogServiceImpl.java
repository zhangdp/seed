package io.github.seed.service.log.impl;

import io.github.seed.common.component.AiTemplate;
import io.github.seed.entity.log.AiChatLog;
import io.github.seed.mapper.log.AiChatLogMapper;
import io.github.seed.model.dto.AiChatInput;
import io.github.seed.model.dto.AiChatOutput;
import io.github.seed.service.log.AiChatLogService;
import lombok.RequiredArgsConstructor;
import org.dromara.hutool.core.lang.Assert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 2024/11/27 ai聊天service实现
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class AiChatLogServiceImpl implements AiChatLogService {

    private final AiChatLogMapper aiChatLogMapper;
    private final AiTemplate aiTemplate;

    @Override
    public AiChatLog getByChatId(String chatId) {
        return aiChatLogMapper.selectByChatId(chatId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(AiChatLog bean) {
        return this.aiChatLogMapper.insert(bean) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean startReceive(String chatId) {
        AiChatLog bean = this.getByChatId(chatId);
        Assert.notNull(bean, "不存在id为" + chatId + "的对话记录");
        AiChatLog update = new AiChatLog();
        update.setId(bean.getId());
        update.setId(bean.getId());
        update.setStartTime(LocalDateTime.now());
        update.setStatus("stream");
        return this.aiChatLogMapper.updateById(update) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean endReceive(String chatId, String replyMessage) {
        AiChatLog bean = this.getByChatId(chatId);
        Assert.notNull(bean, "不存在id为" + chatId + "的对话记录");
        AiChatLog update = new AiChatLog();
        update.setId(bean.getId());
        update.setId(bean.getId());
        update.setEndTime(LocalDateTime.now());
        update.setStatus("end");
        update.setReplyMessage(replyMessage);
        return this.aiChatLogMapper.updateById(update) > 0;
    }

    @Override
    public List<AiChatLog> listBySessionId(String sessionId) {
        return this.aiChatLogMapper.selectBySessionId(sessionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SseEmitter chatStream(AiChatInput input) {
        AiChatLog bean = new AiChatLog();
        bean.setUserId(input.getLoginUser().getId());
        bean.setChatId(input.getChatId());
        bean.setSessionId(input.getSessionId());
        bean.setSendTime(LocalDateTime.now());
        bean.setSendMessage(input.getMessage());
        bean.setStatus("send");
        this.add(bean);
        Flux<String> flux = aiTemplate.chatStream(input.getMessage());
        SseEmitter sse = new SseEmitter(10000L);
        flux.subscribe(i -> {
            AiChatOutput out = new AiChatOutput();
            out.setChatId(input.getChatId());
            out.setSessionId(input.getSessionId());
            out.setMessage(i);
            out.setTimestamp(System.currentTimeMillis());
            out.setType("steam");
            try {
                sse.send(out);
            } catch (IOException e) {
                sse.completeWithError(e);
            }
        }, e -> {
            sse.completeWithError(e);
        }, () -> {
            sse.complete();
        });
        return sse;
    }
}
