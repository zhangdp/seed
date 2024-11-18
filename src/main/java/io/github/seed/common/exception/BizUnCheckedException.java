package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 2023/4/3 业务未检查异常，http状态码会输出正常的200
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
public class BizUnCheckedException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 异常码
     */
    private final int code;

    public BizUnCheckedException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizUnCheckedException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizUnCheckedException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public BizUnCheckedException(ErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
    }

    public BizUnCheckedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.code = errorCode.code();
    }

    public BizUnCheckedException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode.message(), cause, enableSuppression, writableStackTrace);
        this.code = errorCode.code();
    }

}
