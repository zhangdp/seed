package com.zhangdp.seed.common.data;

import com.zhangdp.seed.common.enums.OperateType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 2023/7/31 操作日志事件
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OperateLogEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private OperateType type;
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
    private Map<String, Object> params;
    /**
     * 结果
     */
    private Object result;
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    /**
     * 用户id
     */
    private Long loginUserId;
    /**
     * 请求uri
     */
    private String uri;
    /**
     * http方法
     */
    private String httpMethod;
    /**
     * 浏览器标识
     */
    private String userAgent;
    /**
     * 客户端ip
     */
    private String clientIp;

    public OperateLogEvent(Object source) {
        super(source);
    }
}
