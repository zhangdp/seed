package io.github.seed.model.query;

import io.github.seed.module.sms.data.SmsPriority;
import io.github.seed.module.sms.data.SmsStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 短信日志查询入参
 * <br>所有条件都可为空，为空表示不限
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Schema(title = "短信日志查询入参")
public class SmsLogQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 短信业务编号
     */
    @Schema(title = "短信业务编号", description = "完全匹配")
    private String smsNo;
    /**
     * 业务场景
     */
    @Schema(title = "业务场景", description = "完全匹配，如login、reset_password")
    private String scene;
    /**
     * 手机号
     */
    @Schema(title = "手机号", description = "完全匹配")
    private String mobile;
    /**
     * 发送状态
     *
     * @see SmsStatus
     */
    @Schema(title = "发送状态", description = "0待发送、1发送中、2发送成功、3发送失败")
    private String status;
    /**
     * 优先级
     *
     * @see SmsPriority
     */
    @Schema(title = "优先级", description = "0普通、1重要、2紧急")
    private Integer priority;
    /**
     * 起始时间
     */
    @Schema(title = "起始时间", description = "按创建时间过滤，格式yyyy-MM-dd HH:mm:ss")
    private String startTime;
    /**
     * 结束时间
     */
    @Schema(title = "结束时间", description = "按创建时间过滤，格式yyyy-MM-dd HH:mm:ss")
    private String endTime;
}
