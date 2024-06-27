package com.zhangdp.seed.common.security;

import com.zhangdp.seed.common.enums.ErrorCode;
import com.zhangdp.seed.common.exception.BizException;
import com.zhangdp.seed.model.dto.LoginResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 2024/6/27
 *
 * @author zhangdp
 * @since
 */
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final AuthenticationManager authenticationManager;

    /**
     * 账号、密码登录
     *
     * @param username
     * @param password
     * @return
     */
    public LoginResult doLogin(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            LoginResult result = new LoginResult();
            return result;
        } catch (BadCredentialsException | UsernameNotFoundException e) {
            log.warn("{}-{}", username, e.getMessage());
            throw new BizException(ErrorCode.USERNAME_NOT_FOUND_OR_BAD_CREDENTIALS);
        } catch (DisabledException | LockedException e) {
            log.warn("{}-{}", username, e.getMessage());
            throw new BizException(ErrorCode.ACCOUNT_LOCKED);
        }
    }

}
