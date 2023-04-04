package com.zhangdp.seed.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 2023/4/3 业务异常，会输出http status 500
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    /**
     * 异常码
     */
    private int code = 500;

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    protected BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public BizException(int code) {
        super();
        this.code = code;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BizException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    protected BizException(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
    
}
