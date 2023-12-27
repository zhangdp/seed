package com.zhangdp.seed.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/9/1 令牌信息
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class Token implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访问令牌
     */
    private String accessToken;
    /**
     * 刷新令牌
     */
    private String refreshToken;


}
