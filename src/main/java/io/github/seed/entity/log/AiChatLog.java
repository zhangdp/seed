package io.github.seed.entity.log;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2024/11/27 AI聊天日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName(TableNameConst.AI_CHAT_LOG)
@Schema(title = "AI聊天日志")
public class AiChatLog extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private Long userId;

    private String chatId;

    private String sessionId;
    /**
     * 发送时间
     */
    @Schema(title = "发送时间")
    private LocalDateTime sendTime;
    /**
     * 开始回复时间
     */
    @Schema(title = "开始回复时间")
    private LocalDateTime startTime;
    /**
     * 结束回复时间
     */
    @Schema(title = "结束回复时间")
    private LocalDateTime endTime;
    /**
     * 发送的消息
     */
    @Schema(title = "发送的消息")
    private String sendMessage;
    /**
     * 回复的消息
     */
    @Schema(title = "回复的消息")
    private String replyMessage;
    /**
     * 状态
     */
    @Schema(title = "回复的消息")
    private String status;

}
