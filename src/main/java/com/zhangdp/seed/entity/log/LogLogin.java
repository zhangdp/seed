package com.zhangdp.seed.entity.log;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhangdp.seed.common.constant.CommonConst;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2023/4/14 登陆日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("log_login")
@Schema(description = "登陆日志")
public class LogLogin implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    @Schema(title = "id")
    private Long id;
    /**
     * 登陆时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "登陆时间", description = "格式：" + CommonConst.DATETIME_FORMATTER)
    private LocalDateTime createTime;
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
     * 登陆类型
     */
    @Schema(title = "登陆类型", description = "密码、手机号验证码等")
    private Integer loginType;
    /**
     * 登陆结果
     */
    @Schema(title = "登陆结果")
    private int resultCode;
    /**
     * 客户端ip
     */
    @Schema(title = "客户端ip")
    private String clientIp;
    /**
     * 客户端地区
     */
    @Schema(title = "地区")
    private String clientArea;
    /**
     * userAgent
     */
    @Schema(title = "浏览器标识")
    private String userAgent;

}
