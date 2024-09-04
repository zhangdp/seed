package io.github.seed.event.component;

import io.github.seed.event.annotation.Event;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 2023/4/12 事件aop
 * <br>设置@Order最小让顺序比事务aop先进入但后离开，保证原业务代码事务已提交成功才触发事件
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Aspect
@Slf4j
// 必须最小值+1，最小是spring aop基础必须先执行
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@Component
public class EventAspect {

    /**
     * 事件调度处理器
     */
    private final EventDispatch eventDispatch;
    /**
     * 方法参数解析器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    /**
     * spel解析器
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    @Autowired
    public EventAspect(EventDispatch eventDispatch) {
        this.eventDispatch = eventDispatch;
    }

    /**
     * 事件产生后执行后续动作
     *
     * @param joinPoint
     * @param annotation
     */
    @AfterReturning(value = "@annotation(annotation)", returning = "result")
    public void afterAdvice(JoinPoint joinPoint, Event annotation, Object result) {
        if (log.isDebugEnabled()) {
            log.debug("EventAspect AfterReturning in: joinPoint={}, event={}", joinPoint, annotation);
        }
        boolean flag = true;
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取参数列表
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
            Object[] args = joinPoint.getArgs();
            if (StrUtil.isNotBlank(annotation.condition())) {
                // 设置表达式上下文
                EvaluationContext context = new StandardEvaluationContext();
                context.setVariable("result", result);
                if (parameterNames != null && parameterNames.length > 0) {
                    for (int i = 0; i < parameterNames.length; i++) {
                        context.setVariable(parameterNames[i], args[i]);
                    }
                }
                // 根据表达式获取是否执行
                flag = Boolean.TRUE.equals(spelExpressionParser.parseExpression(annotation.condition()).getValue(context, Boolean.class));
            }
            if (flag) {
                Map<String, Object> params = new LinkedHashMap<>();
                if (parameterNames != null && parameterNames.length > 0) {
                    for (int i = 0; i < parameterNames.length; i++) {
                        params.put(parameterNames[i], args[i]);
                    }
                }
                if (annotation.isAsync()) {
                    eventDispatch.dispatchAsync(annotation.type(), annotation.tag(), params, result, annotation.delay());
                } else {
                    eventDispatch.dispatch(annotation.type(), annotation.tag(), params, result, annotation.delay());
                }
            }
        } catch (Exception e) {
            log.error("EventAspect error: joinPoint=" + joinPoint + ", event=" + annotation, e);
        }
    }
}
