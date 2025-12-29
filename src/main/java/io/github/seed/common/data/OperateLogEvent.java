package io.github.seed.common.data;

import io.github.seed.common.enums.OperateType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 2023/7/31 操作日志事件
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@ToString
public class OperateLogEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    @Setter
    private OperateType type;
    /**
     * 操作描述
     */
    @Setter
    private String title;
    /**
     * 关联模块
     */
    @Setter
    private String refModule;
    /**
     * 关联模块id，spel表达式
     */
    @Setter
    private Long refId;
    /**
     * 调用方法
     */
    @Setter
    private String method;
    /**
     * 参数
     */
    @Setter
    private Map<String, String[]> parameterMap;
    /**
     * 结果
     */
    @Setter
    private Serializable result;
    /**
     * 开始时间
     */
    @Setter
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    @Setter
    private LocalDateTime endTime;
    /**
     * 用户id
     */
    @Setter
    private Long userId;
    /**
     * 请求uri
     */
    @Setter
    private String uri;
    /**
     * http方法
     */
    @Setter
    private String httpMethod;
    /**
     * 浏览器标识
     */
    // private String userAgent;
    /**
     * 客户端ip
     */
    @Setter
    private String clientIp;
    /**
     * 异常
     */
    @Setter
    private Throwable throwable;
    /**
     * 请求体
     */
    @Setter
    private String requestBody;
    /**
     * 请求头
     */
    @Setter
    private Map<String, String> headerMap;

    public OperateLogEvent(Object source) {
        super(source);
    }
}
