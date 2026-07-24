package io.github.seed.common.security;

import cn.hutool.v7.core.lang.Assert;
import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.security.data.LoginUser;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Base64;

/**
 * 认证相关工具类
 *
 * @author zhangdp
 * @since 2024/7/3
 */
public class SecurityUtils {

    /**
     * 从请求中解析出bearer token
     *
     * @param request
     * @return
     */
    public static String resolveBearerToken(HttpServletRequest request) {
        String token = request.getHeader(SecurityConst.AUTHORIZATION_HEADER);
        if (token != null && !(token = token.trim()).isEmpty() && StrUtil.startWithIgnoreCase(token, SecurityConst.AUTH_TYPE_BEARER)) {
            token = token.substring(SecurityConst.AUTH_TYPE_BEARER.length() + 1).trim();
        }
        // 如果header中取不到token则从参数中取
        if (token == null || token.isEmpty()) {
            token = request.getParameter(SecurityConst.AUTHORIZATION_PARAMETER);
            if (token != null) {
                token = token.trim();
            }
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
            if (principal instanceof LoginUser loginUser) {
                return loginUser;
            }
        }
        return null;
    }

    /**
     * 从http header解析Basic认证，结果为数组：[用户名, 密码]
     *
     * @param request
     * @return
     */
    public static String[] resolveBasicAuth(HttpServletRequest request) {
        String header = request.getHeader(SecurityConst.AUTHORIZATION_HEADER);
        if (header == null || (header = header.trim()).isEmpty()) {
            return null;
        }

        return resolveBasicAuth(header);
    }

    /**
     * 解析basic auth字符串，结果为数组：[用户名, 密码]
     *
     * @param basicAuth
     * @return
     */
    public static String[] resolveBasicAuth(String basicAuth) {
        basicAuth = basicAuth.trim();
        if (StrUtil.startWithIgnoreCase(basicAuth, SecurityConst.AUTH_TYPE_BASIC)) {
            basicAuth = basicAuth.substring(SecurityConst.AUTH_TYPE_BASIC.length() + 1).trim();
        }
        String str = new String(Base64.getDecoder().decode(basicAuth));
        int index = str.indexOf(':');
        Assert.isTrue(index > -1, "Invalid Basic Auth format, missing ':' separator");
        return new String[]{str.substring(0, index), str.substring(index + 1)};
    }

    /**
     * 验证basic auth是否正确
     *
     * @param basicAuth
     * @param username
     * @param password
     * @return
     */
    public static boolean validateBasicAuth(String basicAuth, String username, String password) {
        try {
            String[] arr = resolveBasicAuth(basicAuth);
            return arr.length == 2 && arr[0].equals(username) && arr[1].equals(password);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从request header解析出basic auth并验证
     *
     * @param request
     * @param username
     * @param password
     * @return
     */
    public static boolean validateBasicAuth(HttpServletRequest request, String username, String password) {
        try {
            String[] arr = resolveBasicAuth(request);
            return arr != null && arr.length == 2 && arr[0].equals(username) && arr[1].equals(password);
        } catch (Exception e) {
            return false;
        }
    }

}
