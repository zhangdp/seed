package com.zhangdp.seed.common.security;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/1/8 保存在JWT中的用户信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
public class JwtTokenUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long id;
    /**
     * 账号
     */
    private String username;
    /**
     * 昵称
     */
    private String name;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 英文逗号,分隔的角色列表
     */
    private String roles;
}
