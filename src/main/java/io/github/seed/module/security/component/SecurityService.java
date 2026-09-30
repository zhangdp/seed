package io.github.seed.module.security.component;

import io.github.seed.module.security.data.*;
import io.github.seed.common.util.SpringWebContextHolder;
import io.github.seed.model.dto.PasswordLoginParams;
import io.github.seed.model.dto.SmsLoginParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * 认证服务类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final AuthenticationManager authenticationManager;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final TokenService tokenService;

    /**
     * 密码登录
     *
     * @param params
     * @exception Exception
     * @return
     */
    public LoginResult loginByPassword(PasswordLoginParams params) throws Throwable {
        return this.doLogin(new UsernamePasswordAuthenticationToken(params.getUsername(), params.getPassword()));
    }

    /**
     * 短信验证码登录
     * <br>短信验证码的校验由controller方法上的{@code @VerifyCaptcha}切面完成，此处只负责认证
     *
     * @param params
     * @exception Exception
     * @return
     */
    public LoginResult loginBySms(SmsLoginParams params) throws Throwable {
        return this.doLogin(new SmsAuthenticationToken(params.getMobile(), params.getCode()));
    }

    /**
     * 执行登录
     *
     * @param authentication
     * @return
     * @throws Exception
     */
    public LoginResult doLogin(Authentication authentication) throws Throwable {
        HttpServletRequest request = SpringWebContextHolder.getRequest();
        HttpServletResponse response = SpringWebContextHolder.getResponse();
        return this.doLogin(authentication, request, response);
    }

    /**
     * 执行登录
     *
     * @param authentication
     * @param request
     * @param response
     * @exception Exception
     * @return
     */
    public LoginResult doLogin(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        // 暂存提交的认证对象：登录处理器靠它回填日志的类型与账号（认证异常不携带authentication）
        if (request != null) {
            request.setAttribute(SecurityConst.REQUEST_ATTR_LOGIN_AUTHENTICATION, authentication);
        }
        try {
            // 提交到spring security进行认证
            Authentication authResult = authenticationManager.authenticate(authentication);
            // 认证结果
            LoginUser user = (LoginUser) authResult.getPrincipal();
            // 生成token信息
            AccessToken accessToken = tokenService.createToken(user);
            LoginResult loginResult = new LoginResult();
            loginResult.setAccessToken(accessToken.getToken());
            RefreshToken refreshToken = accessToken.getRefreshToken();
            if (refreshToken != null) {
                loginResult.setRefreshToken(refreshToken.getToken());
            }
            loginResult.setExpiresIn(accessToken.getExpiresIn());
            loginResult.setUserId(user.getId());
            loginResult.setUsername(user.getUsername());
            loginResult.setTokenType(SecurityConst.AUTH_TYPE_BEARER);
            // 认证成功事件
            this.authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
            return loginResult;
        } catch (Exception e) {
            // 登录失败，转为自定义异常抛出
            if (e instanceof AuthenticationException authException) {
                if (authException instanceof InternalAuthenticationServiceException) {
                    // InternalAuthenticationServiceException属于服务异常，并不是逻辑上的登录失败，因此直接抛出来源
                    throw authException.getCause();
                } else {
                    // 认证失败事件
                    this.authenticationFailureHandler.onAuthenticationFailure(request, response, authException);
                }
            }
            throw e;
        }
    }

    /**
     * 注销
     *
     * @return
     */
    public boolean logout() {
        HttpServletRequest request = SpringWebContextHolder.getRequest();
        return this.logout(request);
    }

    /**
     * 注销
     *
     * @param request
     * @return
     */
    public boolean logout(HttpServletRequest request) {
        String token = SecurityUtils.resolveBearerToken(request);
        // 没有token不报错也相当于注销成功
        if (token == null || token.isEmpty()) {
            return false;
        }
        // 刷新令牌一并带上才能彻底作废，否则客户端还能拿它换新令牌
        String refreshToken = request.getHeader(SecurityConst.REFRESH_TOKEN_HEADER);
        return this.doLogout(token, refreshToken);
    }

    /**
     * 执行注销
     *
     * @param token        访问令牌
     * @param refreshToken 刷新令牌，可为空
     * @return 是否注销成功
     */
    public boolean doLogout(String token, String refreshToken) {
        return tokenService.removeToken(token, refreshToken);
    }

    /**
     * 把某个用户已签发的令牌全部作废：改密码、禁用账号、管理员踢人
     *
     * @param userId 用户id
     * @return 作废的凭证数量
     */
    public int kickUser(Long userId) {
        return tokenService.removeUserTokens(userId);
    }

    /**
     * 检测token
     *
     * @return
     */
    public boolean checkToken() {
        // filter已经读取了token，此处只需要验证是否有认证通过即可
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated();
    }

    /**
     * 续签
     *
     * @param refreshToken
     * @return
     */
    public LoginResult refreshToken(String refreshToken) throws Throwable {
        Authentication authentication = new RefreshAuthenticationToken(refreshToken);
        return this.doLogin(authentication);
    }

}
