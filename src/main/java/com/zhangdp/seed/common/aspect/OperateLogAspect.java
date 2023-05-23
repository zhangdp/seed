package com.zhangdp.seed.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.entity.log.LogOperate;
import com.zhangdp.seed.service.log.LogOperateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

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
@Component
@Order
public class OperateLogAspect {

    /**
     * 方法参数解析器
     */
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    /**
     * spel表达式解析器
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    @Autowired
    private LogOperateService logOperateService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 忽略的参数类型
     */
    private static final Class[] IGNORE_PARAMS_CLASS = {
            HttpServletRequest.class,
            HttpServletResponse.class,
            MultipartFile.class,
            HttpSession.class
    };

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

        LogOperate lo = new LogOperate();
        Map<String, Object> params = null;
        try {
            params = this.getParamsMap(point, operateLog);
            // 参数需要先设置，不然执行原方法后参数值可能发生改变
            lo.setJsonParams(objectMapper.writeValueAsString(params));
        } catch (Exception e) {
            log.warn("操作日志获取参数失败", e);
        }

        // 执行原方法
        Long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        Long endTime = System.currentTimeMillis();

        try {
            lo.setMethod(method);
            lo.setJsonParams(objectMapper.writeValueAsString(params));
            lo.setCostTime(endTime - startTime);
            if (result != null) {
                lo.setJsonResult(objectMapper.writeValueAsString(result));
            }
            lo.setType(operateLog.type().type());
            lo.setRefModule(StrUtil.nullIfEmpty(operateLog.refModule()));
            lo.setRefId(this.getRefId(operateLog.refId(), params, result));
            lo.setUserId(this.getLoginUserId());
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                lo.setUri(request.getRequestURI());
                lo.setHttpMethod(request.getMethod());
                lo.setUserAgent(request.getHeader("User-Agent"));
                lo.setClientIp(JakartaServletUtil.getClientIP(request));
            }
            logOperateService.save(lo);
            if (log.isDebugEnabled()) {
                log.debug("记录操作日志：{}", lo);
            }
        } catch (Exception e) {
            log.warn("记录操作日志出错", e);
        }

        return result;
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
        if (StrUtil.isNotBlank(spel)) {
            EvaluationContext context = new StandardEvaluationContext();
            if (MapUtil.isNotEmpty(params)) {
                params.forEach(context::setVariable);
            }
            context.setVariable("result", result);
            return spelExpressionParser.parseExpression(spel).getValue(context, Long.class);
        }
        return null;
    }

    /**
     * 获取当前登录用户id
     *
     * @return
     */
    private Long getLoginUserId() {
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (loginId != null) {
            return Long.valueOf(String.valueOf(loginId));
        }
        return null;
    }

    /**
     * 将方法参数放入map中
     *
     * @param point
     * @param operationLog
     * @return
     */
    private Map<String, Object> getParamsMap(ProceedingJoinPoint point, OperateLog operationLog) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        // 获取参数列表
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        Object[] args = point.getArgs();
        Map<String, Object> params = new LinkedHashMap<>(ArrayUtil.length(parameterNames));
        if (ArrayUtil.isNotEmpty(parameterNames)) {
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
        }
        return params;
    }

}
