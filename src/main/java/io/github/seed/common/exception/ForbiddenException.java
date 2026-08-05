package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * 2024/6/27 没权限异常，输出http status 403
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class ForbiddenException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN.value());
    }

    public ForbiddenException(Throwable cause) {
        super(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN.value(), cause);
    }

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN.code(), message, HttpStatus.FORBIDDEN.value());
    }

    public ForbiddenException(String message, Throwable cause) {
        super(ErrorCode.FORBIDDEN.code(), message, HttpStatus.FORBIDDEN.value(), cause);
    }

    public ForbiddenException(int code, String message) {
        super(code, message, HttpStatus.FORBIDDEN.value());
    }

    public ForbiddenException(int code, String message, Throwable cause) {
        super(code, message, HttpStatus.FORBIDDEN.value(), cause);
    }

    public ForbiddenException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, HttpStatus.FORBIDDEN.value(), cause, enableSuppression, writableStackTrace);
    }

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.FORBIDDEN.value());
    }

    public ForbiddenException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, HttpStatus.FORBIDDEN.value(), cause);
    }

    public ForbiddenException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, HttpStatus.FORBIDDEN.value(), cause, enableSuppression, writableStackTrace);
    }
}
