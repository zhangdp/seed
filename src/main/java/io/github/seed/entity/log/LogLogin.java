package io.github.seed.entity.log;

import com.baomidou.mybatisplus.annotation.*;
import io.github.seed.common.constant.Const;
import io.github.seed.common.constant.TableNameConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2023/4/14 登录日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName(TableNameConst.LOG_LOGIN)
@Schema(description = "登录日志")
public class LogLogin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Schema(title = "id", description = "修改时需传")
    private Long id;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "添加时间", description = "格式：" + Const.DATETIME_FORMATTER + "。保存时忽略")
    private LocalDateTime createdDate;
    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private Long userId;
    /**
     * 用户标识、如账号、手机号、邮箱等
     */
    @Schema(title = "用户标识", description = "用户标识、如账号、手机号、邮箱等")
    private String userCode;
    /**
     * 登录类型
     */
    @Schema(title = "登录类型", description = "密码、手机号验证码等")
    private Integer loginType;
    /**
     * 登录结果
     */
    @Schema(title = "登录结果")
    private int resultCode;
    /**
     * 客户端ip
     */
    @Schema(title = "客户端ip")
    private String clientIp;
    /**
     * userAgent
     */
    @Schema(title = "浏览器标识")
    private String userAgent;

}
