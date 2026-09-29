package io.github.seed.module.captcha.annotation;

import io.github.seed.module.captcha.data.CaptchaScene;
import io.github.seed.module.captcha.data.CaptchaType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证码校验注解，标注在controller方法上，方法执行前先校验验证码
 * <br>key与验证码值都通过SpEL从方法入参获取，上下文可用：形参名、{@code #request}、{@code #response}
 * <pre>
 * &#64;VerifyCaptcha(type = CaptchaType.SMS, scene = CaptchaScene.LOGIN,
 *         keyEl = "#params.mobile", codeEl = "#params.code")
 * </pre>
 * 注意：形参名依赖编译期的{@code -parameters}（starter已默认开启）；表达式求值为空时一律判失败
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
     * @return 验证码类型，默认图片验证码
     */
    CaptchaType type() default CaptchaType.IMAGE;

    /**
     * 场景，生成与校验两端必须一致，同时用于隔离不同业务的验证码
     *
     * @return 场景
     */
    String scene() default CaptchaScene.DEFAULT;

    /**
     * 取验证码key的SpEL表达式
     *
     * @return SpEL表达式
     */
    String keyEl();

    /**
     * 取前端提交的验证码值的SpEL表达式
     *
     * @return SpEL表达式
     */
    String codeEl();

    /**
     * 允许失败次数，达到即作废验证码
     *
     * @return 允许失败次数
     */
    int failCount() default 1;

    /**
     * 校验成功后是否删除验证码
     *
     * @return 是否删除
     */
    boolean removeOnSuccess() default true;

    /**
     * 自定义失败提示，为空使用默认提示
     *
     * @return 失败提示
     */
    String message() default "";
}
