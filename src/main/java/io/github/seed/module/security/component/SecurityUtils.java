package io.github.seed.module.security.component;

import cn.hutool.v7.core.lang.Assert;
import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.enums.LoginType;
import io.github.seed.module.security.data.LoginUser;
import io.github.seed.module.security.data.RefreshAuthenticationToken;
import io.github.seed.module.security.data.SecurityConst;
import io.github.seed.module.security.data.SmsAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Base64;

/**
 * 认证相关工具类
 *
 * @author zhangdp
 * @since 2024/7/3
 */
public class SecurityUtils {

    /**
     * 取登录时提交的原始认证对象，由{@code SecurityService}在认证前写入request
     * <br>登录失败时认证异常不携带authentication，只有这里拿得到提交上来的账号
     */
    /**
     * 取当前请求中登录时提交的原始认证对象
     *
     * @param request 当前请求
     * @return 认证对象，没有时返回null
     */
    public static Authentication getLoginAuthentication(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object attribute = request.getAttribute(SecurityConst.REQUEST_ATTR_LOGIN_AUTHENTICATION);
        return attribute instanceof Authentication authentication ? authentication : null;
    }

    /**
     * 推断登录类型，无法识别时返回null
     *
     * @param authentication 认证对象，为null时返回null
     * @return 登录类型
     */
    public static LoginType resolveLoginType(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        if (authentication instanceof RefreshAuthenticationToken) {
            return LoginType.REFRESH_TOKEN;
        }
        if (authentication instanceof SmsAuthenticationToken) {
            return LoginType.SMS;
        }
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            return LoginType.PASSWORD;
        }
        return null;
    }

    /**
     * 解析登录账号（账号、手机号、邮箱等）
     * <br>续签的principal是refresh token明文，不能当账号记录，返回null
     *
     * @param authentication 认证对象，为null时返回null
     * @return 登录账号，取不到时返回null
     */
    public static String resolveUsername(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser loginUser) {
            return loginUser.getUsername();
        }
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        if (authentication instanceof RefreshAuthenticationToken) {
            return null;
        }
        return authentication.getName();
    }

    /**
     * 从请求中解析出bearer token
     *
     * @param request
     * @return
     */
    public static String resolveBearerToken(HttpServletRequest request) {
        String token = request.getHeader(SecurityConst.AUTHORIZATION_HEADER);
        if (token != null && StrUtil.startWithIgnoreCase(token = token.trim(), SecurityConst.AUTH_TYPE_BEARER)) {
            // 头里只有"Bearer"六个字母时substring会越界，这种头视为没带令牌
            token = token.length() > SecurityConst.AUTH_TYPE_BEARER.length()
                    ? token.substring(SecurityConst.AUTH_TYPE_BEARER.length() + 1).trim()
                    : "";
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
