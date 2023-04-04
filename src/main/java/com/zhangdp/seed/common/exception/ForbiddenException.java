package com.zhangdp.seed.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 2023/4/3 权限不足，会输出http status 403
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public class ForbiddenException extends RuntimeException {

    /**
     * 异常码
     */
    private int code = 403;

    public ForbiddenException() {
        super();
    }

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    protected ForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ForbiddenException(int code) {
        super();
        this.code = code;
    }

    public ForbiddenException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ForbiddenException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ForbiddenException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    protected ForbiddenException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
    
}
