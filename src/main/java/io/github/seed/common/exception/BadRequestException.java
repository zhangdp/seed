package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

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
        super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST.value());
    }

    public BadRequestException(Throwable cause) {
        super(ErrorCode.BAD_REQUEST, HttpStatus.BAD_REQUEST.value(), cause);
    }

    public BadRequestException(String message) {
        super(ErrorCode.BAD_REQUEST.code(), message, HttpStatus.BAD_REQUEST.value());
    }

    public BadRequestException(String message, Throwable cause) {
        super(ErrorCode.BAD_REQUEST.code(), message, HttpStatus.BAD_REQUEST.value(), cause);
    }

    public BadRequestException(int code, String message) {
        super(code, message, HttpStatus.BAD_REQUEST.value());
    }

    public BadRequestException(int code, String message, Throwable cause) {
        super(code, message, HttpStatus.BAD_REQUEST.value(), cause);
    }

    public BadRequestException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, HttpStatus.BAD_REQUEST.value(), cause, enableSuppression, writableStackTrace);
    }

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.BAD_REQUEST.value());
    }

    public BadRequestException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, HttpStatus.BAD_REQUEST.value(), cause);
    }

    public BadRequestException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, HttpStatus.BAD_REQUEST.value(), cause, enableSuppression, writableStackTrace);
    }
}
