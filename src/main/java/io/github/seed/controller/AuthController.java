package io.github.seed.controller;

import io.github.seed.module.captcha.annotation.VerifyCaptcha;
import io.github.seed.module.captcha.data.CaptchaScene;
import io.github.seed.module.captcha.data.CaptchaType;
import io.github.seed.module.security.data.IgnoreAuth;
import io.github.seed.module.security.data.LoginResult;
import io.github.seed.module.security.data.LoginUser;
import io.github.seed.module.security.component.SecurityService;
import io.github.seed.model.dto.PermissionTreeNode;
import io.github.seed.model.dto.UserInfo;
import io.github.seed.model.dto.PasswordLoginParams;
import io.github.seed.model.dto.SmsLoginParams;
import io.github.seed.service.sys.PermissionService;
import io.github.seed.service.sys.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证接口
 *
 * @author zhangdp
 * @since 1.0.0
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "认证", description = "认证相关接口如登录、注销等")
public class AuthController {

    private final SecurityService securityService;
    private final UserService userService;
    private final PermissionService permissionService;

    /**
     * 密码登录
     * <br>图形验证码的校验与消费由{@link VerifyCaptcha}切面完成，校验失败不会进入认证流程
     *
     * @param params
     * @return
     */
    @IgnoreAuth
    @PostMapping("/login/password")
    @Operation(summary = "密码登录", description = "需要先调用GET /captcha/image?scene=login获取图形验证码，"
            + "把返回的key作为captchaKey、用户输入的验证码作为code一并提交")
    @VerifyCaptcha(type = CaptchaType.IMAGE, scene = CaptchaScene.LOGIN,
            keyEl = "#params.captchaKey", codeEl = "#params.code")
    public LoginResult loginByPassword(@RequestBody @Valid PasswordLoginParams params) throws Throwable {
        return securityService.loginByPassword(params);
    }

    /**
     * 短信验证码登录
     * <br>验证码的校验与消费由{@link VerifyCaptcha}切面完成，校验失败不会进入认证流程
     *
     * @param params
     * @return
     */
    @IgnoreAuth
    @PostMapping("/login/sms")
    @Operation(summary = "短信验证码登录", description = "需要先调用/captcha/sms发送验证码，同一场景下同一手机号发送间隔内不能重复发送")
    @VerifyCaptcha(type = CaptchaType.SMS, scene = CaptchaScene.LOGIN,
            keyEl = "#params.mobile", codeEl = "#params.code")
    public LoginResult loginBySms(@RequestBody @Valid SmsLoginParams params) throws Throwable {
        return securityService.loginBySms(params);
    }

    /**
     * 注销
     *
     * @param request
     */
    @IgnoreAuth
    @DeleteMapping("/logout")
    @Operation(summary = "注销", description = "无论结果如何，前端都当做注销成功清除本地token")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean logout(HttpServletRequest request) {
        return securityService.logout(request);
    }

    /**
     * 检测token
     *
     * @return
     */
    @PostMapping("/token/check")
    @Operation(summary = "检测token", description = "检测当前token是否有效，有效时返回true，无效时响应401")
    public boolean checkToken() {
        return securityService.checkToken();
    }

    /**
     * 续签
     *
     * @param refreshToken
     * @return
     * @throws Exception
     */
    @IgnoreAuth
    @PostMapping("/token/refresh")
    @Operation(summary = "续签", description = "使用refresh_token续签token，成功时与登录接口无异，失败时响应401")
    public LoginResult refreshToken(String refreshToken) throws Throwable {
        return securityService.refreshToken(refreshToken);
    }

    /**
     * 获取当前登录用户的菜单树列表
     *
     * @param loginUser
     * @return
     */
    @GetMapping("/user/menus")
    @Operation(summary = "获取登录用户的菜单树", description = "获取当前登录用户的菜单树列表")
    public List<PermissionTreeNode> userMenus(LoginUser loginUser) {
        return permissionService.listUserMenuTrees(loginUser.getId());
    }

    /**
     * 获取当前登录用户的详细信息
     *
     * @param loginUser
     * @return
     */
    @GetMapping("/user/info")
    @Operation(summary = "获取登录用户信息", description = "获取当前登录用户的详细信息")
    public UserInfo userInfo(LoginUser loginUser) {
        return userService.getInfo(loginUser.getId());
    }

}
