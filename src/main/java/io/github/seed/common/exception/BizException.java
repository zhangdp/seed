package io.github.seed.common.exception;

import io.github.seed.common.enums.ErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 2023/4/3 业务异常，http状态码会输出正常的200
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

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
    }

    public BizException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.code = errorCode.code();
    }

    public BizException(ErrorCode errorCode, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(errorCode.message(), cause, enableSuppression, writableStackTrace);
        this.code = errorCode.code();
    }

}
