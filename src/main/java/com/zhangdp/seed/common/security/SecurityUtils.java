package com.zhangdp.seed.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 认证相关工具类
 *
 * @author zhangdp
 * @since 2024/7/3
 */
public class SecurityUtils {

    /**
     * 从请求中解析出token
     *
     * @param request
     * @return
     */
    public static String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(SecurityConst.AUTHORIZATION_HEADER);
        if (StrUtil.isNotBlank(token)) {
            token = token.substring(SecurityConst.AUTH_TYPE_BEARER.length() + 1);
        }
        // 如果header中没有token，则从请求参数中获取
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(SecurityConst.AUTHORIZATION_PARAMETER);
        }
        return token;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                return (LoginUser) principal;
            }
        }
        return null;
    }

}
