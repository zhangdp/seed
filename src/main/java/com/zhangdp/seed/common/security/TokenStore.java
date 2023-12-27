package com.zhangdp.seed.common.security;

import com.zhangdp.seed.common.data.Token;

/**
 * 2023/9/1 token存储接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
public interface TokenStore {

    /**
     * 创建令牌
     *
     * @return
     */
    Token createToken();


}
