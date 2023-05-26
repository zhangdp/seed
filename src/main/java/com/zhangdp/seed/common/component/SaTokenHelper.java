package com.zhangdp.seed.common.component;

import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.*;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.enums.ErrorCode;

/**
 * 2023/5/25 sa-token过滤器失败处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class SaTokenHelper {

    /**
     * 解析satoken 异常转换为返回对象
     *
     * @param e
     * @return
     */
    public static R<?> resolveError(SaTokenException e) {
        R<?> r;
        if (e instanceof NotLoginException) {
            r = resolveNoLoginError((NotLoginException) e);
        } else if (e instanceof NotBasicAuthException) {
            r = new R<>(ErrorCode.NOT_BASIC_AUTH);
        } else if (e instanceof NotPermissionException) {
            r = new R<>(ErrorCode.FORBIDDEN_NO_PERMISSION);
        } else if (e instanceof NotRoleException) {
            r = new R<>(ErrorCode.FORBIDDEN_NO_ROLE);
        } else {
            r = new R<>(ErrorCode.BAD_REQUEST, e.getLocalizedMessage());
        }
        return r;
    }

    /**
     * 解析satoken 未登录异常
     *
     * @param e
     * @return
     */
    public static R<?> resolveNoLoginError(NotLoginException e) {
        return switch (e.getCode()) {
            case SaErrorCode.CODE_11001, SaErrorCode.CODE_11011 -> new R<>(ErrorCode.TOKEN_NOT_FOUND);
            case SaErrorCode.CODE_11012 -> new R<>(ErrorCode.TOKEN_INVALID);
            case SaErrorCode.CODE_11013 -> new R<>(ErrorCode.TOKEN_EXPIRED);
            case SaErrorCode.CODE_11014 -> new R<>(ErrorCode.TOKEN_REPLACED);
            case SaErrorCode.CODE_11015 -> new R<>(ErrorCode.TOKEN_KICK_OUT);
            default -> new R<>(ErrorCode.UNAUTHORIZED);
        };
    }

}
