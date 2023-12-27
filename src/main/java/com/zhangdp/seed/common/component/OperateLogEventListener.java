package com.zhangdp.seed.common.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhangdp.seed.common.data.OperateLogEvent;
import com.zhangdp.seed.entity.log.LogOperation;
import com.zhangdp.seed.service.log.LogOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hutool.core.date.TimeUtil;
import org.dromara.hutool.core.exception.ExceptionUtil;
import org.dromara.hutool.core.map.MapUtil;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 2023/7/31 操作日志事件监听器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OperateLogEventListener {

    /**
     * 操作日志service
     */
    private final LogOperationService logOperationService;
    /**
     * jackson json工具类
     */
    private final ObjectMapper objectMapper;

    /**
     * 监听OperateLogEvent事件
     *
     * @param event
     */
    @Async
    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = OperateLogEvent.class) // 不起作用
    @EventListener(OperateLogEvent.class)
    public void onEvent(OperateLogEvent event) {
        try {
            if (log.isDebugEnabled()) {
                log.debug("收到OperateLogEvent: {}", event);
            }
            LogOperation lo = new LogOperation();
            lo.setTitle(event.getTitle());
            lo.setCreateTime(event.getStartTime());
            lo.setUserId(event.getUserId());
            lo.setType(event.getType().type());
            lo.setUri(event.getUri());
            lo.setHttpMethod(event.getHttpMethod());
            lo.setUserAgent(event.getUserAgent());
            lo.setClientIp(event.getClientIp());
            lo.setMethod(event.getMethod());
            lo.setCostTime(TimeUtil.between(event.getStartTime(), event.getEndTime()).toMillis());
            lo.setRefModule(event.getRefModule());
            lo.setRefId(event.getRefId());
            lo.setSucceed(event.isSucceed());
            if (event.getThrowable() != null) {
                lo.setErrorStrace(ExceptionUtil.stacktraceToString(event.getThrowable(), 65535));
            }
            if (event.getResult() != null) {
                // lo.setResultCode(event.getResult() instanceof R<?> r ? r.getCode() : CommonConst.RESULT_SUCCESS);
                lo.setResult(event.getResult() instanceof String s ? s : objectMapper.writeValueAsString(event.getResult()));
            }
            if (MapUtil.isNotEmpty(event.getParams())) {
                lo.setParams(objectMapper.writeValueAsString(event.getParams()));
            }
            logOperationService.save(lo);
        } catch (Exception e) {
            log.error("操作日志事件处理失败，event=" + event, e);
        }
    }

}
