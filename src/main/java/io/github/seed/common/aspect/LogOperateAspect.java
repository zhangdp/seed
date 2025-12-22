package io.github.seed.common.aspect;

import cn.hutool.v7.core.text.StrUtil;
import cn.hutool.v7.http.server.servlet.ServletUtil;
import io.github.seed.common.constant.Const;
import io.github.seed.common.util.SpELUtils;
import io.github.seed.common.util.SpringWebContextHolder;
import io.github.seed.common.annotation.LogOperation;
import io.github.seed.common.data.OperateLogEvent;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.data.LoginUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.DefaultParameterNameDiscoverer;

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
public class LogOperateAspect {

    /**
     * 方法参数解析器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    /**
     * spring事件发布器
     */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 环绕拥有@OperationLog 注解的controller方法
     *
     * @param point
     * @param logOperation
     * @return
     * @throws Throwable
     */
    @Around("within(io.github.seed.controller..*) && @annotation(logOperation)")
    public Object around(ProceedingJoinPoint point, LogOperation logOperation) throws Throwable {
        String method = point.getTarget().getClass().getName() + "." + point.getSignature().getName();
        log.debug("OperateLogAspect around: method={}, annotation={}", method, logOperation);

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
            if (!hasError || logOperation.logIfError()) {
                LocalDateTime endTime = LocalDateTime.now();
                try {
                    HttpServletRequest request = Objects.requireNonNull(SpringWebContextHolder.getRequest());
                    OperateLogEvent event = new OperateLogEvent(point);
                    // 结果必须可序列化
                    if (logOperation.logResult() && result instanceof Serializable s) {
                        event.setResult(s);
                    }
                    event.setStartTime(startTime);
                    event.setEndTime(endTime);
                    event.setType(logOperation.type());
                    event.setTitle(logOperation.title());
                    event.setRefModule(logOperation.refModule());
                    event.setMethod(method);
                    event.setStartTime(startTime);
                    event.setThrowable(throwable);
                    event.setUri(request.getRequestURI());
                    event.setHttpMethod(request.getMethod());
                    // event.setUserAgent(request.getHeader("User-Agent"));
                    event.setClientIp(ServletUtil.getClientIP(request));
                    if (logOperation.logRequestBody()) {
                        event.setRequestBody(ServletUtil.getBody(request));
                    }
                    if (logOperation.logParameter()) {
                        event.setParameterMap(request.getParameterMap());
                    }
                    if (logOperation.logHeader()) {
                        Map<String, String> headers = new HashMap<>();
                        Enumeration<String> headerNames = request.getHeaderNames();
                        while (headerNames.hasMoreElements()) {
                            String name = headerNames.nextElement();
                            String value = request.getHeader(name);
                            headers.put(name, value);
                        }
                        event.setHeaderMap(headers);
                    }
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    if (loginUser != null) {
                        event.setUserId(loginUser.getId());
                    }
                    if (StrUtil.isNotBlank(logOperation.refIdEl())) {
                        Map<String, Object> context = new LinkedHashMap<>();
                        Object[] args = point.getArgs();
                        MethodSignature signature = (MethodSignature) point.getSignature();
                        // 获取参数列表
                        String[] argNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
                        if (argNames != null && argNames.length > 0) {
                            for (int i = 0; i < argNames.length; i++) {
                                String name = argNames[i];
                                Object obj = args[i];
                                context.put(name, obj);
                            }
                        }
                        context.put(Const.EL_RESULT, result);
                        Long refId = SpELUtils.parseExpression(logOperation.refIdEl(), context, Long.class);
                        event.setRefId(refId);
                    }
                    // 发出事件
                    applicationEventPublisher.publishEvent(event);
                    log.debug("发出操作日志事件：{}", event);
                } catch (Exception e) {
                    log.warn("发出操作日志事件出错", e);
                }
            }
        }
    }

}
