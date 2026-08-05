package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * 2023/4/3 自定义业务异常，可自定义http状态码
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 异常码
     */
    private final int code;
    /**
     * http状态码
     */
    private final int httpStatus;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.httpStatus = HttpStatus.OK.value();
    }

    public BizException(int code, String message, int httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = HttpStatus.OK.value();
    }

    public BizException(int code, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BizException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.httpStatus = HttpStatus.OK.value();
    }

    public BizException(int code, String message, int httpStatus, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
        this.httpStatus = HttpStatus.OK.value();
    }

    public BizException(ErrorCode errorCode, int httpStatus) {
        super(errorCode.message());
        this.code = errorCode.code();
        this.httpStatus = httpStatus;
    }

    public BizException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.code = errorCode.code();
        this.httpStatus = HttpStatus.OK.value();
    }

    public BizException(ErrorCode errorCode, int httpStatus, Throwable cause) {
        super(errorCode.message(), cause);
        this.code = errorCode.code();
        this.httpStatus = httpStatus;
    }

    public BizException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode.message(), cause, enableSuppression, writableStackTrace);
        this.code = errorCode.code();
        this.httpStatus = HttpStatus.OK.value();
    }

    public BizException(ErrorCode errorCode, int httpStatus, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode.message(), cause, enableSuppression, writableStackTrace);
        this.code = errorCode.code();
        this.httpStatus = httpStatus;
    }

}
