package io.github.seed.mapper.log;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.seed.entity.log.AiChatLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 2024/11/27 AI聊天记录mapper
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Mapper
public interface AiChatLogMapper extends BaseMapper<AiChatLog> {

    /**
     * 根据chatId查询单条记录
     *
     * @param chatId
     * @return
     */
    default AiChatLog selectByChatId(String chatId) {
        return this.selectOne(new LambdaQueryWrapper<AiChatLog>().eq(AiChatLog::getChatId, chatId));
    }

    /**
     * 根据sessionId获取对话列表
     *
     * @param sessionId
     * @return
     */
    default List<AiChatLog> selectBySessionId(String sessionId) {
        return this.selectList(new LambdaQueryWrapper<AiChatLog>()
                .eq(AiChatLog::getSessionId, sessionId)
                .orderByAsc(AiChatLog::getSendTime));
    }
}
