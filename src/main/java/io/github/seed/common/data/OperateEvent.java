package io.github.seed.common.data;

import io.github.seed.common.enums.OperateType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 操作事件
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OperateEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private OperateType type;
    /**
     * 操作描述
     */
    private String description;
    /**
     * 关联模块
     */
    private String refModule;
    /**
     * 关联模块id，spel表达式
     */
    private Long refId;
    /**
     * 调用方法
     */
    private String method;
    /**
     * 参数
     */
    private Map<String, String[]> parameterMap;
    /**
     * 结果
     */
    private Serializable result;
    /**
     * 返回状态码
     */
    private Integer resultCode;
    /**
     * 开始时间
     */
    private LocalDateTime startAt;
    /**
     * 结束时间
     */
    private LocalDateTime endAt;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 请求uri
     */
    private String uri;
    /**
     * 浏览器标识
     */
    // private String userAgent;
    /**
     * 客户端ip
     */
    private String clientIp;
    /**
     * 异常
     */
    private Throwable throwable;
    /**
     * 请求体
     */
    private String body;
    /**
     * 请求头
     */
    private Map<String, String> headerMap;

    public OperateEvent(Object source) {
        super(source);
    }
}
