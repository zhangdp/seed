package com.zhangdp.seed.common.exception;

import com.zhangdp.seed.common.enums.ErrorCode;

import java.io.Serial;

/**
 * 2024/6/27 未登录异常
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class UnauthorizedException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UnauthorizedException(int code, String message) {
        super(code, message);
    }

    public UnauthorizedException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public UnauthorizedException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, cause, enableSuppression, writableStackTrace);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthorizedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public UnauthorizedException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, cause, enableSuppression, writableStackTrace);
    }
}
