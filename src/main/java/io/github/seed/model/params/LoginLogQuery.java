package io.github.seed.model.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/10/9 登录日志查询入参
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "登录日志查询入参")
public class LoginLogQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 登录人id
     */
    @Schema(title = "登录人id")
    private Long userId;
    /**
     * 起始时间
     */
    @Schema(title = "起始时间", description = "格式yyyy-MM-dd HH:mm:ss")
    private String startTime;
    /**
     * 结束时间
     */
    @Schema(title = "结束时间", description = "格式yyyy-MM-dd HH:mm:ss")
    private String endTime;
    /**
     * 登录类型
     */
    @Schema(title = "登录类型")
    private String loginType;
}
