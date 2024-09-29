package io.github.seed.common.aspect;

import io.github.seed.common.SpringWebMvcContextHolder;
import io.github.seed.common.annotation.OperateLog;
import io.github.seed.common.data.OperateLogEvent;
import io.github.seed.security.SecurityUtils;
import io.github.seed.security.data.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.server.servlet.ServletUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

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
     * 方法参数解析器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    /**
     * spel表达式解析器
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /**
     * spring事件发布器
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 环绕拥有@OperationLog 注解的controller方法
     *
     * @param point
     * @param operateLog
     * @return
     * @throws Throwable
     */
    @Around("within(io.github.seed.controller..*) && @annotation(operateLog)")
    public Object around(ProceedingJoinPoint point, OperateLog operateLog) throws Throwable {
        String method = point.getTarget().getClass().getName() + "." + point.getSignature().getName();
        log.debug("OperateLogAspect around: method={}, annotation={}", method, operateLog);

        Object result = null;
        boolean hasError = false;
        Throwable throwable = null;
        LocalDateTime startTime = LocalDateTime.now();

        try {
            // 执行原方法
            result = point.proceed();
            return result;
        } catch (Throwable t) {
            hasError = true;
            throwable = t;
            throw t;
        } finally {
            // 失败时只有logIfError为true才记录日志
            if (!hasError || operateLog.logIfError()) {
                LocalDateTime endTime = LocalDateTime.now();
                try {
                    HttpServletRequest request = Objects.requireNonNull(SpringWebMvcContextHolder.getRequest());
                    OperateLogEvent event = new OperateLogEvent(point);
                    // 结果必须可序列化
                    if (operateLog.logResult() && result instanceof Serializable s) {
                        event.setResult(s);
                    }
                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    event.setType(operateLog.type());
                    event.setTitle(operateLog.title());
                    event.setRefModule(operateLog.refModule());
                    event.setMethod(method);
                    event.setStartTime(startTime);
                    event.setThrowable(throwable);
                    event.setUri(request.getRequestURI());
                    event.setHttpMethod(request.getMethod());
                    // event.setUserAgent(request.getHeader("User-Agent"));
                    event.setClientIp(ServletUtil.getClientIP(request));
                    if (operateLog.logRequestBody() && request instanceof ContentCachingRequestWrapper requestWrapper) {
                        event.setRequestBody(requestWrapper.getContentAsString());
                    }
                    if (operateLog.logParameter()) {
                        event.setParameterMap(request.getParameterMap());
                    }
                    if (operateLog.logHeader()) {
                        Map<String, String> headers = new HashMap<>();
                        Enumeration<String> headerNames = request.getHeaderNames();
                        while (headerNames.hasMoreElements()) {
                            String name = headerNames.nextElement();
                            headers.put(name, request.getHeader(name));
                        }
                        event.setHeaderMap(headers);
                    }
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    if (loginUser != null) {
                        event.setUserId(loginUser.getId());
                    }
                    if (StrUtil.isNotBlank(operateLog.refIdEl())) {
                        EvaluationContext context = new StandardEvaluationContext();
                        Object[] args = point.getArgs();
                        MethodSignature signature = (MethodSignature) point.getSignature();
                        // 获取参数列表
                        String[] argNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
                        if (argNames != null && argNames.length > 0) {
                            for (int i = 0; i < argNames.length; i++) {
                                String name = argNames[i];
                                Object obj = args[i];
                                context.setVariable(name, obj);
                            }
                        }
                        context.setVariable("result", result);
                        Long refId = spelExpressionParser.parseExpression(operateLog.refIdEl()).getValue(context, Long.class);
                        event.setRefId(refId);
                    }
                    // 发出事件
                    applicationEventPublisher.publishEvent(event);
                } catch (Exception e) {
                    log.warn("发出操作日志事件出错", e);
                }
            }
        }
    }

}
