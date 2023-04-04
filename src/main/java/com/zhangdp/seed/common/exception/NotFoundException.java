package com.zhangdp.seed.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 2023/4/3 找不到资源异常，会输出http status 404
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public class NotFoundException extends RuntimeException {

    /**
     * 异常码
     */
    private int code = 404;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    protected NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NotFoundException(int code) {
        super();
        this.code = code;
    }

    public NotFoundException(int code, String message) {
        super(message);
        this.code = code;
    }

    public NotFoundException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public NotFoundException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    protected NotFoundException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
    
}
