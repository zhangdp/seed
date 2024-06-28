package com.zhangdp.seed.model.dto;

import com.zhangdp.seed.common.constant.CommonConst;
import com.zhangdp.seed.common.security.LoginUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/7 登录结果
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Schema(description = "登录结果")
public class LoginResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    @Schema(title = "访问令牌", description = "后续请求API时需设置Authorization Header")
    private String accessToken;
    /**
     * 令牌类型
     */
    @Schema(title = "令牌类型")
    private String tokenType = CommonConst.BEARER_TYPE;
    /**
     * 刷新令牌
     */
    private String refreshToken;
    /**
     * 剩余访问令牌有效期，单位：秒
     */
    private Integer expiresIn;
    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private Long userId;
    /**
     * 用户
     */
    // @Schema(title = "登录用户信息")
    // private LoginUser user;
    /**
     * 账号
     */
    @Schema(title = "账号")
    private String username;
    /**
     * 名称
     */
    @Schema(title = "名称")
    private String name;
}
