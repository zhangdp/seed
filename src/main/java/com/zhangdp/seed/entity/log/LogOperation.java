package com.zhangdp.seed.entity.log;

import com.baomidou.mybatisplus.annotation.*;
import com.zhangdp.seed.common.ValidGroup;
import com.zhangdp.seed.common.constant.Const;
import com.zhangdp.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 2023/4/17 操作日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("log_operation")
@Schema(description = "操作日志")
public class LogOperation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @Schema(title = "id")
    private Long id;
    /**
     * 操作描述
     */
    @Schema(title = "操作描述")
    private String title;
    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private Long userId;
    /**
     * 操作类型
     *
     * @see com.zhangdp.seed.common.enums.OperateType
     */
    @Schema(title = "操作类型")
    private String type;
    /**
     * 请求uri
     */
    @Schema(title = "请求uri路径")
    private String uri;
    /**
     * 请求的http 方式
     *
     * @return
     */
    @Schema(title = "http方式")
    private String httpMethod;
    /**
     * 用户浏览器标识
     */
    // @Schema(title = "用户浏览器标识")
    // private String userAgent;
    /**
     * 客户端ip
     */
    @Schema(title = "客户端ip")
    private String clientIp;
    /**
     * 执行的方法
     */
    @Schema(title = "执行的方法")
    private String method;
    /**
     * 操作时间
     */
    @Schema(title = "操作时间")
    private LocalDateTime operateTime;
    /**
     * 耗时
     */
    @Schema(title = "耗时")
    private Long costTime;
    /**
     * 关联模块类型
     */
    @Schema(title = "关联模块类型")
    private String refModule;
    /**
     * 关联模块id
     */
    @Schema(title = "关联模块id")
    private Long refId;
    /**
     * 返回结果标识
     */
    // @Schema(title = "返回结果状态码")
    // private Integer resultCode;
    /**
     * 是否成功
     */
    // @Schema(title = "是否成功")
    // private Boolean succeed;
    /**
     * json格式的返回值
     */
    @Schema(title = "返回值")
    private String result;
    /**
     * json格式的入参
     */
    @Schema(title = "入参")
    private String parameters;
    /**
     * 请求体
     */
    @Schema(title = "请求体")
    private String requestBody;
    /**
     * 请求头
     */
    @Schema(title = "请求头")
    private String headers;
    /**
     * 异常堆栈
     */
    @Schema(title = "异常堆栈")
    private String errorStrace;
    /**
     * 添加时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "添加时间")
    protected LocalDateTime createdDate;
}
