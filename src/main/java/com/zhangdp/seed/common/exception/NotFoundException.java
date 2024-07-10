package com.zhangdp.seed.common.exception;

import com.zhangdp.seed.common.enums.ErrorCode;

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

    public NotFoundException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public NotFoundException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, cause, enableSuppression, writableStackTrace);
    }

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public NotFoundException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, cause, enableSuppression, writableStackTrace);
    }
}
