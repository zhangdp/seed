package com.zhangdp.seed.common.exception;

import java.io.Serial;

/**
 * 2023/6/12 不存在资源异常
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class NotFoundException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotFoundException(int code, String message) {
        super(code, message);
    }
}
