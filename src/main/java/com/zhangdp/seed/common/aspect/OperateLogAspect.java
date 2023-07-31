package com.zhangdp.seed.common.aspect;

import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.common.component.SecurityHelper;
import com.zhangdp.seed.common.data.OperateLogEvent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 2023/4/17 记录操作日志aop
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Aspect
@Slf4j
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

    private final SecurityHelper securityHelper;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 环绕拥有@OperationLog 注解的类
     *
     * @param point
     * @param operateLog
     * @return
     * @throws Throwable
     */
    @Around("@annotation(operateLog)")
    public Object around(ProceedingJoinPoint point, OperateLog operateLog) throws Throwable {
        String method = point.getTarget().getClass().getName() + "." + point.getSignature().getName() + "()";
        if (log.isDebugEnabled()) {
            log.debug("OperateLogAspect Around:{}", method);
        }

        // 执行原方法
        LocalDateTime startTime = LocalDateTime.now();
        Object result = point.proceed();
        LocalDateTime endTime = LocalDateTime.now();

        try {
            Map<String, Object> params = this.toParamsMap(point, operateLog);
            OperateLogEvent event = new OperateLogEvent(point.getTarget());
            event.setType(operateLog.type());
            event.setRefModule(operateLog.refModule());
            if (StrUtil.isNotBlank(operateLog.refIdEl())) {
                event.setRefId(this.getRefId(operateLog.refIdEl(), params, result));
            }
            event.setMethod(method);
            event.setParams(params);
            event.setResult(result);
            event.setStartTime(startTime);
            event.setEndTime(endTime);
            event.setLoginUserId(securityHelper.loginUserIdDefaultNull());
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                event.setUri(request.getRequestURI());
                event.setHttpMethod(request.getMethod());
                event.setUserAgent(request.getHeader("User-Agent"));
                event.setClientIp(JakartaServletUtil.getClientIP(request));
            }
            // 发出事件
            applicationEventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.warn("发出操作日志事件出错", e);
        }

        return result;
    }

    /**
     * 将方法参数放入map中
     *
     * @param point
     * @param operationLog
     * @return
     */
    private Map<String, Object> toParamsMap(ProceedingJoinPoint point, OperateLog operationLog) {
        Object[] args = point.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            // 获取参数列表
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
            Map<String, Object> params = new LinkedHashMap<>(ArrayUtil.length(parameterNames));
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
