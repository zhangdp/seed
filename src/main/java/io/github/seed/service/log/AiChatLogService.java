package io.github.seed.service.log;

import io.github.seed.entity.log.AiChatLog;
import io.github.seed.model.dto.AiChatInput;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 2024/11/27 ai聊天service接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface AiChatLogService {

    /**
     * 根据chatId查询
     *
     * @param chatId
     * @return
     */
    AiChatLog getByChatId(String chatId);

    /**
     * 新增
     *
     * @param bean
     * @return
     */
    boolean add(AiChatLog bean);

    /**
     * 开始接收
     *
     * @param chatId
     * @return
     */
    boolean startReceive(String chatId);

    /**
     * 结束接收
     *
     * @param chatId
     * @param replyMessage
     * @return
     */
    boolean endReceive(String chatId, String replyMessage);

    /**
     * 根据sessionId获取对话列表
     *
     * @param sessionId
     * @return
     */
    List<AiChatLog> listBySessionId(String sessionId);

    /**
     * 流式对话，返回sse
     *
     * @param input
     * @return
     */
    SseEmitter chatStream(AiChatInput input);

}
