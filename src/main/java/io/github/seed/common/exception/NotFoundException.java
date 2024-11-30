package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;

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

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }

    public NotFoundException(Throwable cause) {
        super(ErrorCode.NOT_FOUND, cause);
    }

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND.code(), message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(ErrorCode.NOT_FOUND.code(), message, cause);
    }

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
