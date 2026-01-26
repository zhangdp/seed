package io.github.seed.common.aspect;

import cn.hutool.v7.core.text.StrUtil;
import io.github.seed.common.constant.Const;
import io.github.seed.common.enums.SensitiveType;
import io.github.seed.common.security.SecurityConst;
import io.github.seed.common.util.SpELUtils;
import io.github.seed.common.util.SpringWebContextHolder;
import io.github.seed.common.annotation.RecordOperationLog;
import io.github.seed.common.data.OperateEvent;
import io.github.seed.common.security.SecurityUtils;
import io.github.seed.common.security.data.LoginUser;
import io.github.seed.common.util.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 记录操作日志aop
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RecordOperationLogAspect {

    /**
     * 方法参数解析器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 环绕拥有@OperationLog 注解的controller方法
     *
     * @param point
     * @param recordOperationLog
     * @return
     * @throws Throwable
     */
    @Around("within(io.github.seed.controller..*) && @annotation(recordOperationLog)")
    public Object around(ProceedingJoinPoint point, RecordOperationLog recordOperationLog) throws Throwable {
        String method = point.getTarget().getClass().getName() + "." + point.getSignature().getName();
        log.debug("OperateLogAspect around: method={}, annotation={}", method, recordOperationLog);

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
            if (!hasError || recordOperationLog.recordIfError()) {
                LocalDateTime endTime = LocalDateTime.now();
                try {
                    HttpServletRequest request = SpringWebContextHolder.getRequest();
                    OperateEvent event = new OperateEvent(point.getSignature());
                    // 结果必须可序列化
                    if (recordOperationLog.logResult() && result instanceof Serializable s) {
                        event.setResult(s);
                    }
                    event.setStartAt(startTime);
                    event.setEndAt(endTime);
                    event.setType(recordOperationLog.type());
                    event.setDescription(recordOperationLog.description());
                    event.setRefModule(recordOperationLog.refModule());
                    event.setMethod(method);
                    event.setThrowable(throwable);
                    event.setUri(request.getRequestURI());
                    // event.setUserAgent(request.getHeader("User-Agent"));
                    event.setClientIp(WebUtils.getClientIP(request));
                    if (recordOperationLog.logRequestBody()) {
                        event.setBody(WebUtils.getBody(request));
                    }
                    if (recordOperationLog.logParameter()) {
                        event.setParameterMap(request.getParameterMap());
                    }
                    if (recordOperationLog.logHeader()) {
                        Map<String, String> headers = new HashMap<>();
                        Enumeration<String> headerNames = request.getHeaderNames();
                        while (headerNames.hasMoreElements()) {
                            String name = headerNames.nextElement();
                            String value = request.getHeader(name);
                            // Authorization认证头脱敏
                            if (SecurityConst.AUTHORIZATION_HEADER.equalsIgnoreCase(name) && value != null && value.length() >= 20) {
                                value = SensitiveType.AUTHORIZATION.getDesensitizer().apply(value);
                            }
                            headers.put(name, value);
                        }
                        event.setHeaderMap(headers);
                    }
                    LoginUser loginUser = SecurityUtils.getLoginUser();
                    if (loginUser != null) {
                        event.setUserId(loginUser.getId());
                    }
                    if (StrUtil.isNotBlank(recordOperationLog.refIdEl())) {
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
                        Long refId = SpELUtils.parseExpression(recordOperationLog.refIdEl(), context, Long.class);
                        event.setRefId(refId);
                    }
                    // 保存操作日志上下文，后续处理结束后记录日志
                    request.setAttribute(Const.REQUEST_ATTR_OPERATION, event);
                } catch (Exception e) {
                    log.warn("保存操作日志上下文出错", e);
                }
            }
        }
    }

}
