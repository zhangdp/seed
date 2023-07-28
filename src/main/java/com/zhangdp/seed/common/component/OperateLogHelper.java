package com.zhangdp.seed.common.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.R;
import com.zhangdp.seed.common.annotation.OperateLog;
import com.zhangdp.seed.entity.log.LogOperate;
import com.zhangdp.seed.service.log.LogOperateService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.map.MapUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.dromara.hutool.http.server.servlet.JakartaServletUtil;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 2023/7/28 操作日志帮助类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OperateLogHelper {
    /**
     * spel表达式解析器
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

    private final LogOperateService logOperateService;
    private final ObjectMapper objectMapper;

    /**
     * 异步记录操作日志
     *
     * @param operateLog
     * @param method
     * @param params
     * @param result
     * @param costTime
     * @param loginUserId
     * @param request
     */
    @Async
    public void log(OperateLog operateLog, String method, Map<String, Object> params, Object result, Long costTime, Long loginUserId, HttpServletRequest request) {
        try {
            LogOperate lo = new LogOperate();
            if (params != null) {
                lo.setJsonParams(objectMapper.writeValueAsString(params));
                lo.setRefId(this.getRefId(operateLog.refIdEl(), params, result));
            }
            lo.setMethod(method);
            lo.setCostTime(costTime);
            if (result != null) {
                lo.setJsonResult(objectMapper.writeValueAsString(result));
                if (result instanceof R<?> r) {
                    lo.setResultCode(r.getCode());
                }
            }
            lo.setType(operateLog.type().type());
            lo.setRefModule(StrUtil.nullIfEmpty(operateLog.refModule()));
            lo.setUserId(loginUserId);
            lo.setUri(request.getRequestURI());
            lo.setHttpMethod(request.getMethod());
            lo.setUserAgent(request.getHeader("User-Agent"));
            lo.setClientIp(JakartaServletUtil.getClientIP(request));
            logOperateService.save(lo);
            if (log.isDebugEnabled()) {
                log.debug("记录操作日志：{}", lo);
            }
        } catch (Exception e) {
            log.error("记录操作日志失败, method=" + method, e);
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

}
