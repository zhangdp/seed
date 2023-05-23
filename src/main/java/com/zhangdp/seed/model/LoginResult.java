package com.zhangdp.seed.model;

import com.zhangdp.seed.model.dto.UserInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/4/7 登陆结果
 *
 * @author zhangdp
 * @since
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
    private String tokenType;
    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private Long userId;
    /**
     * 用户
     */
    @Schema(title = "用户信息")
    private UserInfo user;
}
