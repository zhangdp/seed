package io.github.seed.entity.log;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.LOGIN_LOG)
@Schema(description = "登录日志")
public class LoginLog extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    @Schema(description = "登录时间，格式：" + Const.DATETIME_FORMATTER + "。保存时忽略")
    private LocalDateTime loginTime;
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;
    /**
     * 用户标识、如账号、手机号、邮箱等
     */
    @Schema(description = "用户标识、如账号、手机号、邮箱等")
    private String username;
    /**
     * 登录类型
     */
    @Schema(description = "登录类型：sms、password")
    private Integer loginType;
    /**
     * 登录结果
     */
    @Schema(description = "登录结果")
    private int resultCode;
    /**
     * 客户端ip
     */
    @Schema(description = "客户端ip")
    private String clientIp;
    /**
     * userAgent
     */
    @Schema(description = "浏览器标识")
    private String userAgent;

}
