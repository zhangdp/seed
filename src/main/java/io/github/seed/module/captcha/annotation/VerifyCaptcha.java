package io.github.seed.module.captcha.annotation;

import io.github.seed.module.captcha.data.CaptchaScene;
import io.github.seed.module.captcha.data.CaptchaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证码校验注解，标注在controller方法上，方法执行前会先校验验证码
 * <br>验证码key与验证码值都通过SpEL表达式从方法入参中获取，可用的上下文变量：
 * <ul>
 *     <li>每个形参名对应的参数值，如{@code #params}、{@code #mobile}</li>
 *     <li>{@code #request}：HttpServletRequest</li>
 *     <li>{@code #response}：HttpServletResponse</li>
 * </ul>
 * 使用示例：
 * <pre>
 * // 入参是@RequestBody的对象
 * &#64;VerifyCaptcha(type = CaptchaType.SMS, scene = CaptchaScene.LOGIN,
 *         keyEl = "#params.mobile", codeEl = "#params.code")
 * public LoginResult loginBySms(&#64;RequestBody &#64;Valid SmsLoginParams params)
 *
 * // 验证码key是前端传参，如手机号、图形验证码返回的唯一key
 * keyEl = "#request.getParameter('captchaKey')"
 * </pre>
 * 注意：
 * <ul>
 *     <li>形参名依赖编译期的{@code -parameters}（spring-boot-starter-parent已默认开启），请勿关闭</li>
 *     <li>表达式求值为null或空白时一律判校验失败，避免表达式写错导致校验被静默跳过</li>
 *     <li>一个方法只能标注一次</li>
 * </ul>
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VerifyCaptcha {

    /**
     * 验证码类型
     *
     * @return
     */
    CaptchaType type() default CaptchaType.IMAGE;

    /**
     * 验证码场景，生成与校验两端必须一致，同时用于隔离不同业务的验证码
     *
     * @return
     */
    String scene() default CaptchaScene.DEFAULT;

    /**
     * 获取验证码key的SpEL表达式
     *
     * @return
     */
    String keyEl();

    /**
     * 获取前端传上来的验证码值的SpEL表达式
     *
     * @return
     */
    String codeEl();

    /**
     * 允许失败次数，达到即作废验证码，默认1次
     *
     * @return
     */
    int failCount() default 1;

    /**
     * 校验成功后是否删除验证码
     *
     * @return
     */
    boolean removeOnSuccess() default true;

    /**
     * 校验失败时的自定义提示，为空使用默认提示
     *
     * @return
     */
    String message() default "";
}
