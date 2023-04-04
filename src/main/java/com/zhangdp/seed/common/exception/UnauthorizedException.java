package com.zhangdp.seed.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 2023/4/3 未登录，会输出http status 401
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public class UnauthorizedException extends RuntimeException {

    /**
     * 异常码
     */
    private int code = 401;

    public UnauthorizedException() {
        super();
    }

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }

    protected UnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnauthorizedException(int code) {
        super();
        this.code = code;
    }

    public UnauthorizedException(int code, String message) {
        super(message);
        this.code = code;
    }

    public UnauthorizedException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public UnauthorizedException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    protected UnauthorizedException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
    
}
