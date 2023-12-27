package com.zhangdp.seed.common.aspect;

import com.zhangdp.seed.common.SpringWebMvcContextHolder;
import com.zhangdp.seed.common.annotation.OperationLog;
import com.zhangdp.seed.common.component.SecurityHelper;
import com.zhangdp.seed.common.data.OperateLogEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.dromara.hutool.core.array.ArrayUtil;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.reflect.ClassUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.server.servlet.JakartaServletUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 2023/4/17 记录操作日志aop
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class OperateLogAspect {

    /**
     * 忽略的参数类型
     */
    private static final Class<?>[] IGNORE_PARAMS_CLASS = {
            HttpServletRequest.class,
            HttpServletResponse.class,
            MultipartFile.class,
            HttpSession.class
    };
    /**
     * 方法参数解析器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    /**
     * spel表达式解析器
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /**
     * 认证相关类
     */
    private final SecurityHelper securityHelper;
    /**
     * spring事件发布器
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 环绕拥有@OperationLog 注解的controller方法
     *
     * @param point
     * @param operationLog
     * @return
     * @throws Throwable
     */
    @Around("within(com.zhangdp.seed.controller..*) && @annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        HttpServletRequest request = Objects.requireNonNull(SpringWebMvcContextHolder.getRequest());
        String uri = request.getRequestURI();
        String method = point.getTarget().getClass().getName() + "." + point.getSignature().getName();
        if (log.isDebugEnabled()) {
            log.debug("OperateLogAspect Around: uri={}, method={}", uri, method);
        }

        Object result = null;
        LocalDateTime startTime = LocalDateTime.now();
        OperateLogEvent event = new OperateLogEvent(point.getTarget());
        event.setType(operationLog.type());
        event.setTitle(operationLog.title());
        event.setRefModule(operationLog.refModule());
        event.setMethod(method);
        event.setStartTime(startTime);
        event.setUserId(securityHelper.loginUserIdDefaultNull());
        event.setUri(request.getRequestURI());
        event.setHttpMethod(request.getMethod());
        event.setUserAgent(request.getHeader("User-Agent"));
        event.setClientIp(JakartaServletUtil.getClientIP(request));

        try {
            // 执行原方法
            result = point.proceed();
            event.setSucceed(true);
            if (operationLog.logResult() && result instanceof Serializable s) {
                event.setResult(s);
            }
            return result;
        } catch (Throwable t) {
            event.setSucceed(false);
            if (operationLog.logIfError()) {
                event.setThrowable(t);
            }
            throw t;
        } finally {
            // 失败时只有logIfError为true才记录日志
            if (event.isSucceed() || operationLog.logIfError()) {
                LocalDateTime endTime = LocalDateTime.now();
                event.setEndTime(endTime);
                try {
                    LinkedHashMap<String, Object> params = this.toParamsMap(point, operationLog);
                    if (operationLog.logParams()) {
                        event.setParams(params);
                    }
                    if (StrUtil.isNotBlank(operationLog.refIdEl())) {
                        event.setRefId(this.getRefId(operationLog.refIdEl(), params, result));
                    }
                    // 发出事件
                    applicationEventPublisher.publishEvent(event);
                    if (log.isDebugEnabled()) {
                        log.debug("发出操作日志事件：{}", event);
                    }
                } catch (Exception e) {
                    log.warn("发出操作日志事件出错", e);
                }
            }
        }
    }

    /**
     * 将方法参数放入map中
     *
     * @param point
     * @param operationLog
     * @return
     */
    private LinkedHashMap<String, Object> toParamsMap(JoinPoint point, OperationLog operationLog) {
        Object[] args = point.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            // 获取参数列表
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
            LinkedHashMap<String, Object> params = new LinkedHashMap<>(ArrayUtil.length(parameterNames));
            for (int i = 0; i < parameterNames.length; i++) {
                String name = parameterNames[i];
                Object obj = args[i];
                if (ArrayUtil.contains(operationLog.ignoreParams(), name)) {
                    continue;
                }
                Class<?> clazz = ClassUtil.getClass(obj);
                if (clazz != null) {
                    if (Arrays.stream(IGNORE_PARAMS_CLASS).anyMatch(c -> c.isAssignableFrom(clazz))) {
                        continue;
                    }
                }
                params.put(name, obj);
            }
            return params;
        } catch (Exception e) {
            log.warn("操作日志获取入参失败", e);
            return null;
        }
    }

    /**
     * 通过spel获取refId
     *
     * @param spel
     * @param params
     * @param result
     * @return
     */
    private Long getRefId(String spel, Map<String, Object> params, Object result) {
        EvaluationContext context = new StandardEvaluationContext();
        if (MapUtil.isNotEmpty(params)) {
            params.forEach(context::setVariable);
        }
        context.setVariable("result", result);
        return spelExpressionParser.parseExpression(spel).getValue(context, Long.class);
    }

}
