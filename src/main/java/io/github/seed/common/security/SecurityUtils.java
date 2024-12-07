package io.github.seed.common.security;

import io.github.seed.common.security.data.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.hutool.core.lang.Assert;
import org.dromara.hutool.core.text.StrUtil;
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

    private static final String BASIC_PREFIX = "Basic";

    /**
     * 从请求中解析出token
     *
     * @param request
     * @return
     */
    public static String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(SecurityConst.AUTHORIZATION_HEADER);
        if (StrUtil.isNotBlank(token) && StrUtil.startWithIgnoreCase(token, SecurityConst.AUTH_TYPE_BEARER)) {
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

    /**
     * 解析basic auth字符串，结果为数组：[用户名, 密码]
     *
     * @param basicAuth
     * @return
     */
    public static String[] resolveBasicAuth(String basicAuth) {
        basicAuth = basicAuth.trim();
        Assert.isTrue(StrUtil.startWithIgnoreCase(basicAuth, BASIC_PREFIX), basicAuth + " is not a valid Basic Auth string");
        String auth = basicAuth.substring(BASIC_PREFIX.length() + 1);
        String str = new String(Base64.getDecoder().decode(auth));
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
        String[] arr = resolveBasicAuth(basicAuth);
        return arr[0].equals(username) && arr[1].equals(password);
    }

}
