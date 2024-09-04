package io.github.seed.entity.log;

import com.baomidou.mybatisplus.annotation.TableName;
import io.github.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/14 登录日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@TableName("log_login")
@Schema(description = "登录日志")
public class LogLogin extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
