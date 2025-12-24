package io.github.seed.common.security.service;

import cn.hutool.v7.core.util.RandomUtil;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.data.*;
import io.github.seed.common.util.SpringWebContextHolder;
import io.github.seed.model.params.LoginParams;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 2024/6/27 认证服务类
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
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public LoginResult login(LoginParams loginParams) throws ServletException, IOException {
        HttpServletRequest request = SpringWebContextHolder.getRequest();
        HttpServletResponse response = SpringWebContextHolder.getResponse();
        return this.login(loginParams, request, response);
    }

    /**
     * 登录
     *
     * @param loginParams
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public LoginResult login(LoginParams loginParams, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Authentication authentication = switch (loginParams.getLoginType()) {
            case PASSWORD -> new UsernamePasswordAuthenticationToken(loginParams.getUsername(), loginParams.getPassword());
            case SMS -> new SmsAuthenticationToken(loginParams.getUsername(), loginParams.getCode());
            default -> throw new IllegalArgumentException("不支持的登录方式：" + loginParams.getLoginType());
        };
        return this.doLogin(authentication, request, response);
    }

    /**
     * 执行登录
     *
     * @param authentication
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public LoginResult doLogin(Authentication authentication) throws ServletException, IOException {
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
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public LoginResult doLogin(Authentication authentication, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 提交到spring security进行认证
            Authentication authResult = authenticationManager.authenticate(authentication);
            // 认证成功处理器
            authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
        } catch (AuthenticationException e) {
            // 认证失败处理器
            authenticationFailureHandler.onAuthenticationFailure(request, response, e);
        }

        // 此处直接返回null，在登录成功处理器统一处理统一response
        return null;
    }

    /**
     * 执行注销
     *
     * @return
     */
    public boolean logout(HttpServletRequest request) {
        String token = SecurityUtils.resolveToken(request);
        if (token == null || token.isEmpty()) {
            return false;
        }
        return this.logout(token);
    }

    /**
     * 执行注销
     *
     * @param token
     * @return
     */
    public boolean logout(String token) {
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
     * @throws ServletException
     * @throws IOException
     */
    public LoginResult refreshToken(String refreshToken) throws ServletException, IOException {
        HttpServletRequest request = SpringWebContextHolder.getRequest();
        HttpServletResponse response = SpringWebContextHolder.getResponse();
        return this.refreshToken(refreshToken, request, response);
    }

    /**
     * 续签
     *
     * @param refreshToken
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public LoginResult refreshToken(String refreshToken, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Authentication authentication = new RefreshAuthenticationToken(refreshToken);
        return this.doLogin(authentication, request, response);
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
