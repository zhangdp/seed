package io.github.seed.common.component;

import cn.hutool.v7.core.date.TimeUtil;
import cn.hutool.v7.core.exception.ExceptionUtil;
import cn.hutool.v7.core.map.MapUtil;
import io.github.seed.common.data.OperateEvent;
import io.github.seed.entity.log.OperationLog;
import io.github.seed.service.log.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

/**
 * 操作日志事件监听器
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OperateEventListener {

    /**
     * 操作日志service
     */
    private final OperationLogService operationLogService;
    /**
     * jackson json工具类
     */
    private final JsonMapper jsonMapper;

    /**
     * 监听OperateLogEvent事件
     *
     * @param event
     */
    @Async
    // @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, classes = OperateLogEvent.class) // 不起作用
    @EventListener(OperateEvent.class)
    public void onEvent(OperateEvent event) {
        try {
            log.debug("收到OperateEvent: {}", event);
            OperationLog lo = new OperationLog();
            lo.setCreatedAt(LocalDateTime.now());
            lo.setDescription(event.getDescription());
            lo.setOperatedAt(event.getStartAt());
            lo.setUserId(event.getUserId());
            lo.setType(event.getType().type());
            lo.setRequestUri(event.getUri());
            lo.setResultCode(event.getResultCode());
            // lo.setUserAgent(event.getUserAgent());
            lo.setClientIp(event.getClientIp());
            lo.setMethod(event.getMethod());
            lo.setCostTime(TimeUtil.between(event.getStartAt(), event.getEndAt()).toMillis());
            lo.setRefModule(event.getRefModule());
            lo.setRefId(event.getRefId());
            if (event.getThrowable() != null) {
                lo.setErrorStrace(ExceptionUtil.stacktraceToString(event.getThrowable(), 1024));
            }
            if (event.getResult() != null) {
                // lo.setResultCode(event.getResult() instanceof R<?> r ? r.getCode() : CommonConst.RESULT_SUCCESS);
                lo.setResult(event.getResult() instanceof String s ? s : jsonMapper.writeValueAsString(event.getResult()));
            }
            if (MapUtil.isNotEmpty(event.getParameterMap())) {
                lo.setRequestParams(jsonMapper.writeValueAsString(event.getParameterMap()));
            }
            if (MapUtil.isNotEmpty(event.getHeaderMap())) {
                lo.setRequestHeaders(jsonMapper.writeValueAsString(event.getHeaderMap()));
            }
            lo.setRequestBody(event.getBody());
            operationLogService.add(lo);
        } catch (Exception e) {
            log.error("操作日志事件处理失败，event: {}", event, e);
        }
    }

}
