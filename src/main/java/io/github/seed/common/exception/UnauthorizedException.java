package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * 2024/6/27 未登录异常，输出http status 401
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class UnauthorizedException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value());
    }

    public UnauthorizedException(Throwable cause) {
        super(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), cause);
    }

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED.code(), message, HttpStatus.UNAUTHORIZED.value());
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(ErrorCode.UNAUTHORIZED.code(), message, HttpStatus.UNAUTHORIZED.value(), cause);
    }

    public UnauthorizedException(int code, String message) {
        super(code, message, HttpStatus.UNAUTHORIZED.value());
    }

    public UnauthorizedException(int code, String message, Throwable cause) {
        super(code, message, HttpStatus.UNAUTHORIZED.value(), cause);
    }

    public UnauthorizedException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, HttpStatus.UNAUTHORIZED.value(), cause, enableSuppression, writableStackTrace);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.UNAUTHORIZED.value());
    }

    public UnauthorizedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, HttpStatus.UNAUTHORIZED.value(), cause);
    }

    public UnauthorizedException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, HttpStatus.UNAUTHORIZED.value(), cause, enableSuppression, writableStackTrace);
    }
}
