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
        // 暂存本次登录提交的认证对象：登录成功/失败处理器要靠它回填登录日志的登录类型与账号
        // （认证异常通常不携带authentication，失败时否则无从得知提交的用户名）
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
            loginResult.setName(user.getName());
            loginResult.setAvatar(user.getAvatar());
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
        return this.doLogout(token);
    }

    /**
     * 执行注销
     *
     * @param token
     * @return
     */
    public boolean doLogout(String token) {
        return tokenService.removeToken(token);
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
