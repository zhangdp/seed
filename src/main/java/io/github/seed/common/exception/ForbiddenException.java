package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;

import java.io.Serial;

/**
 * 2024/6/27 没权限异常
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class ForbiddenException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ForbiddenException(int code, String message) {
        super(code, message);
    }

    public ForbiddenException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public ForbiddenException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, cause, enableSuppression, writableStackTrace);
    }

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ForbiddenException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ForbiddenException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, cause, enableSuppression, writableStackTrace);
    }
}
