package io.github.seed.module.captcha.aspect;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.module.captcha.annotation.VerifyCaptcha;
import io.github.seed.module.captcha.CaptchaService;
import io.github.seed.common.enums.ErrorCode;
import io.github.seed.common.exception.BizException;
import io.github.seed.common.util.SpELUtils;
import io.github.seed.common.util.SpringWebContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 验证码校验切面：环绕{@link VerifyCaptcha}标注的方法，先校验再执行，
 * 失败直接抛{@link BizException}，因此不会触发操作日志切面
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class CaptchaVerifyAspect {

    private final CaptchaService captchaService;

    /**
     * 方法参数名解析器，用于把形参名作为SpEL的上下文变量
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 环绕拥有@VerifyCaptcha注解的controller方法
     *
     * @param point         连接点
     * @param verifyCaptcha 方法上的注解
     * @return 原方法的返回值
     * @throws Throwable 原方法或验证码校验抛出的异常
     */
    @Around("within(io.github.seed.controller..*) && @annotation(verifyCaptcha)")
    public Object around(ProceedingJoinPoint point, VerifyCaptcha verifyCaptcha) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Map<String, Object> context = this.buildContext(signature, point.getArgs());
        String key = this.evaluate(verifyCaptcha.keyEl(), context, "keyEl");
        String code = this.evaluate(verifyCaptcha.codeEl(), context, "codeEl");
        if (StrUtil.isBlank(key) || StrUtil.isBlank(code)) {
            // fail-closed：表达式取不到值一律判校验失败，避免表达式写错导致校验被静默跳过
            log.debug("验证码校验失败-验证码key或验证码值为空：method={}, keyEl={}, codeEl={}",
                    signature.getMethod(), verifyCaptcha.keyEl(), verifyCaptcha.codeEl());
            throw new BizException(ErrorCode.CAPTCHA_EXPIRED.code(),
                    StrUtil.defaultIfBlank(verifyCaptcha.message(), ErrorCode.CAPTCHA_EXPIRED.message()));
        }
        captchaService.verify(verifyCaptcha.type(), verifyCaptcha.scene(), key, code, verifyCaptcha.failCount(),
                verifyCaptcha.removeOnSuccess(), verifyCaptcha.message());
        return point.proceed();
    }

    /**
     * 构建SpEL上下文：形参名-参数值，以及request、response
     *
     * @param signature 方法签名
     * @param args      方法入参值
     * @return SpEL上下文
     */
    private Map<String, Object> buildContext(MethodSignature signature, Object[] args) {
        Map<String, Object> context = new LinkedHashMap<>();
        String[] names = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        if (names != null) {
            for (int i = 0; i < names.length && i < args.length; i++) {
                context.put(names[i], args[i]);
            }
        }
        try {
            HttpServletRequest request = SpringWebContextHolder.getRequest();
            context.put("request", request);
            HttpServletResponse response = SpringWebContextHolder.getResponse();
            context.put("response", response);
        } catch (Exception e) {
            // 非web环境，忽略
            log.debug("获取request、response失败，忽略", e);
        }
        return context;
    }

    /**
     * 解析SpEL表达式，解析失败或结果为空白时返回null
     *
     * @param expression 表达式
     * @param context    SpEL上下文
     * @param name       表达式用途，用于日志
     * @return 解析结果，取不到时返回null
     */
    private String evaluate(String expression, Map<String, Object> context, String name) {
        if (StrUtil.isBlank(expression)) {
            return null;
        }
        try {
            Object value = SpELUtils.parseExpression(expression, context, Object.class);
            return value == null ? null : value.toString().trim();
        } catch (Exception e) {
            log.error("@VerifyCaptcha 解析{}表达式出错，expression={}", name, expression, e);
            return null;
        }
    }

}
