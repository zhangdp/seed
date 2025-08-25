package io.github.seed.common.component;

import cn.hutool.v7.core.date.TimeUtil;
import cn.hutool.v7.core.exception.ExceptionUtil;
import cn.hutool.v7.core.map.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.seed.common.data.OperateLogEvent;
import io.github.seed.entity.log.OperationLog;
import io.github.seed.service.log.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
    private final OperationLogService operationLogService;
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
            log.debug("收到OperateLogEvent: {}", event);
            OperationLog lo = new OperationLog();
            lo.setCreatedDate(LocalDateTime.now());
            lo.setTitle(event.getTitle());
            lo.setOperateTime(event.getStartTime());
            lo.setUserId(event.getUserId());
            lo.setType(event.getType().type());
            lo.setUri(event.getUri());
            lo.setHttpMethod(event.getHttpMethod());
            // lo.setUserAgent(event.getUserAgent());
            lo.setClientIp(event.getClientIp());
            lo.setMethod(event.getMethod());
            lo.setCostTime(TimeUtil.between(event.getStartTime(), event.getEndTime()).toMillis());
            lo.setRefModule(event.getRefModule());
            lo.setRefId(event.getRefId());
            if (event.getThrowable() != null) {
                lo.setErrorStrace(ExceptionUtil.stacktraceToString(event.getThrowable(), 1024));
            }
            if (event.getResult() != null) {
                // lo.setResultCode(event.getResult() instanceof R<?> r ? r.getCode() : CommonConst.RESULT_SUCCESS);
                lo.setResult(event.getResult() instanceof String s ? s : objectMapper.writeValueAsString(event.getResult()));
            }
            if (MapUtil.isNotEmpty(event.getParameterMap())) {
                lo.setParameters(objectMapper.writeValueAsString(event.getParameterMap()));
            }
            if (MapUtil.isNotEmpty(event.getHeaderMap())) {
                lo.setHeaders(objectMapper.writeValueAsString(event.getHeaderMap()));
            }
            lo.setRequestBody(event.getRequestBody());
            operationLogService.add(lo);
        } catch (Exception e) {
            log.error("操作日志事件处理失败，event: {}", event, e);
        }
    }

}
