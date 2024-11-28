package io.github.seed.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/11/27 AI对话输出
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Schema(title = "AI对话输出")
public class AiChatOutput implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对话id
     */
    @Schema(title = "对话id")
    private String chatId;
    /**
     * 会话id
     */
    @Schema(title = "会话id")
    private String sessionId;
    /**
     * 消息
     */
    @Schema(title = "消息")
    private String message;
    /**
     * 时间戳
     */
    @Schema(title = "时间戳")
    private Long timestamp;
    /**
     * 类型
     */
    @Schema(title = "类型", description = "begin：开始、stream：传输、end：结束")
    private String type;
}
