package io.github.seed.entity.sys;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.annotation.Sensitive;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.SensitiveType;
import io.github.seed.module.sms.data.SmsPriority;
import io.github.seed.module.sms.data.SmsStatus;
import io.github.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短信日志
 * <br>短信发送采用「新增记录 -> 异步发送 -> 回写状态」两段式，本表即为该过程的载体，
 * 也是后续基于手机号/场景做发送频率限制与统计的数据来源
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.SYS_SMS_LOG)
@Schema(description = "短信日志")
public class SmsLog extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 短信业务编号，新增时由服务端生成，用于发送结果回写与问题排查
     */
    @Schema(description = "短信业务编号")
    private String smsNo;
    /**
     * 业务场景，如login、reset_password
     */
    @Schema(description = "业务场景，如login、reset_password")
    private String scene;
    /**
     * 手机号
     */
    @Schema(description = "手机号，查询接口返回时会脱敏")
    @Sensitive(SensitiveType.MOBILE)
    private String mobile;
    /**
     * 短信签名
     */
    @Schema(description = "短信签名")
    private String signName;
    /**
     * 短信模板编码，文本短信时为空
     */
    @Schema(description = "短信模板编码，文本短信时为空")
    private String templateCode;
    /**
     * 短信模板参数，json格式，保持插入顺序
     */
    @Schema(description = "短信模板参数，json格式")
    private String templateParams;
    /**
     * 短信文本内容，模板短信时为空
     */
    @Schema(description = "短信文本内容，模板短信时为空")
    private String content;
    /**
     * 发送状态
     *
     * @see SmsStatus
     */
    @Schema(description = "发送状态：0待发送、1发送中、2发送成功、3发送失败")
    private int status;
    /**
     * 优先级，越大越优先发送
     * <br>包装类型而非int：ORM按实体更新只写非空字段，用int会被回写冲成0，重试时高优先级就退化成普通了
     *
     * @see SmsPriority
     */
    @Schema(description = "优先级：0普通、1重要、2紧急，越大越优先发送")
    private Integer priority;
    /**
     * 发送失败次数：每次发送失败自增，未达到最大重试次数时状态退回待发送等待重试，
     * 达到上限后置为发送失败终态
     * <br>用包装类型而非int：ORM按实体更新时只写非空字段，避免抢占等非发送场景的更新把失败次数冲成0
     */
    @Schema(description = "发送失败次数")
    private Integer retryCount;
    /**
     * 服务商请求id
     */
    @Schema(description = "服务商请求id")
    private String requestId;
    /**
     * 服务商回执id
     */
    @Schema(description = "服务商回执id")
    private String bizId;
    /**
     * 服务商返回码
     */
    @Schema(description = "服务商返回码")
    private String resultCode;
    /**
     * 服务商返回描述，失败时为失败原因
     */
    @Schema(description = "服务商返回描述，失败时为失败原因")
    private String resultMessage;
    /**
     * 发送时间，即实际调用服务商的时间
     */
    @Schema(description = "发送时间，格式：" + Const.DATETIME_FORMATTER)
    private LocalDateTime sendAt;

}
