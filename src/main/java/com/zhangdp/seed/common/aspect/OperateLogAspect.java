package com.zhangdp.seed.common.aspect;

import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.common.component.OperateLogHelper;
import com.zhangdp.seed.common.component.SecurityHelper;
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
import org.dromara.hutool.core.reflect.ClassUtil;
import org.springframework.core.DefaultParameterNameDiscoverer;
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

    private final OperateLogHelper operateLogHelper;
    private final SecurityHelper securityHelper;

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

        Map<String, Object> params = this.toParamsMap(point, operateLog);

        // 执行原方法
        Long startTime = System.currentTimeMillis();
        Object result = point.proceed();
        Long endTime = System.currentTimeMillis();

        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            operateLogHelper.log(operateLog, method, params, result, endTime - startTime, securityHelper.loginUserIdDefaultNull(), request);
        } catch (Exception e) {
            log.warn("记录操作日志出错", e);
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
        try {
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
        } catch (Exception e) {
            log.warn("操作日志获取入参失败", e);
            return null;
        }
    }

}
