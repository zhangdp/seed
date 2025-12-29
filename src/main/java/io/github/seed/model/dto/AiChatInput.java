package io.github.seed.model.dto;

import io.github.seed.common.security.data.LoginUser;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/11/27 AI对话入参
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "AI聊天入参")
public class AiChatInput implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对话id
     */
    @NotBlank(message = "对话id不能为空")
    @Schema(title = "对话id")
    private String chatId;
    /**
     * 会话id
     */
    @NotBlank(message = "会话id不能为空")
    @Schema(title = "会话id")
    private String sessionId;
    /**
     * 消息
     */
    @NotBlank(message = "消息不能为空")
    @Schema(title = "消息")
    private String message;
    /**
     * 用户
     */
    @Schema(title = "用户", hidden = true)
    private LoginUser loginUser;

}
