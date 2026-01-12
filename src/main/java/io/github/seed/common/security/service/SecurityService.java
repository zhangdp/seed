package io.github.seed.common.security.service;

import cn.hutool.v7.core.util.RandomUtil;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.UnauthorizedException;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.data.*;
import io.github.seed.common.util.SpringWebContextHolder;
import io.github.seed.model.params.LoginParams;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
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
     * 登录
     *
     * @param loginParams
     * @exception Exception
     * @return
     */
    public LoginResult login(LoginParams loginParams) throws Throwable {
        Authentication authentication = switch (loginParams.getLoginType()) {
            case PASSWORD ->
                    new UsernamePasswordAuthenticationToken(loginParams.getUsername(), loginParams.getPassword());
            case SMS -> new SmsAuthenticationToken(loginParams.getUsername(), loginParams.getCode());
            default -> throw new IllegalArgumentException("不支持的登录方式：" + loginParams.getLoginType());
        };
        return this.doLogin(authentication);
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
        String token = SecurityUtils.resolveToken(request);
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

    /**
     * 生成并发送登录的短信验证码
     *
     * @param mobile
     * @return
     */
    public boolean loginSms(String mobile) {
        String code = RandomUtil.randomNumbers(6);
        // todo
        return false;
    }

}
