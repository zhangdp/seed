package com.zhangdp.seed.common.component;

import cn.dev33.satoken.exception.SaTokenException;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.enums.ErrorCode;

/**
 * 2023/5/25 sa-token过滤器失败处理器
 *
 * @author zhangdp
 * @since 1.0.0
 */
public class SaTokenErrorHandler {

    /**
     * satoken 异常转换为返回对象
     *
     * @param e
     * @return
     */
    public static R<?> resolveError(SaTokenException e) {
        return new R<>(ErrorCode.BAD_REQUEST, e.getLocalizedMessage());
    }

}
