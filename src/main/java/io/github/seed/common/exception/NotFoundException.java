package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * 2023/6/12 不存在资源异常，输出http status 404
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class NotFoundException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND.value());
    }

    public NotFoundException(Throwable cause) {
        super(ErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND.value(), cause);
    }

    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND.code(), message, HttpStatus.NOT_FOUND.value());
    }

    public NotFoundException(String message, Throwable cause) {
        super(ErrorCode.NOT_FOUND.code(), message, HttpStatus.NOT_FOUND.value(), cause);
    }

    public NotFoundException(int code, String message) {
        super(code, message, HttpStatus.NOT_FOUND.value());
    }

    public NotFoundException(int code, String message, Throwable cause) {
        super(code, message, HttpStatus.NOT_FOUND.value(), cause);
    }

    public NotFoundException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, HttpStatus.NOT_FOUND.value(), cause, enableSuppression, writableStackTrace);
    }

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.NOT_FOUND.value());
    }

    public NotFoundException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, HttpStatus.NOT_FOUND.value(), cause);
    }

    public NotFoundException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, HttpStatus.NOT_FOUND.value(), cause, enableSuppression, writableStackTrace);
    }
}
