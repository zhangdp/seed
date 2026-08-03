package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;

import java.io.Serial;

/**
 * 非法请求异常，输出http status 400
 *
 * @author zhangdp
 * @since 2026/8/3
 */
public class BadRequestException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public BadRequestException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public BadRequestException(Throwable cause) {
        super(ErrorCode.BAD_REQUEST, cause);
    }

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST.code(), message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(ErrorCode.BAD_REQUEST.code(), message, cause);
    }

    public BadRequestException(int code, String message) {
        super(code, message);
    }

    public BadRequestException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BadRequestException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, cause, enableSuppression, writableStackTrace);
    }

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public BadRequestException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, cause, enableSuppression, writableStackTrace);
    }
}
