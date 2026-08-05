package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * 自定义系统异常，输出http status 500
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class InternalServerException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    public InternalServerException(int code, String message) {
        super(code, message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
    
    public InternalServerException(int code, String message, Throwable cause) {
        super(code, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
    }

    public InternalServerException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(code, message, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause, enableSuppression, writableStackTrace);
    }

    public InternalServerException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public InternalServerException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
    }

    public InternalServerException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause, enableSuppression, writableStackTrace);
    }

    public InternalServerException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public InternalServerException(Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
    }

    public InternalServerException(String message) {
        super(ErrorCode.INTERNAL_SERVER_ERROR.code(), message, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public InternalServerException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR.code(), message, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
    }


}
