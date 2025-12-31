package io.github.seed.entity.log;

import com.mybatisflex.annotation.Table;
import io.github.seed.common.constant.TableNameConst;
import io.github.seed.common.enums.OperateType;
import io.github.seed.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(TableNameConst.LOG_OPERATION)
@Schema(description = "操作日志")
public class OperationLog extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 操作描述
     */
    @Schema(description = "操作描述")
    private String title;
    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;
    /**
     * 操作类型
     *
     * @see OperateType
     */
    @Schema(description = "操作类型")
    private String type;
    /**
     * 请求uri
     */
    @Schema(description = "请求uri路径")
    private String uri;
    /**
     * 用户浏览器标识
     */
    // @Schema(description = "用户浏览器标识")
    // private String userAgent;
    /**
     * 请求的http 方式
     */
    @Schema(description = "http方式")
    private String httpMethod;
    /**
     * 客户端ip
     */
    @Schema(description = "客户端ip")
    private String clientIp;
    /**
     * 执行的方法
     */
    @Schema(description = "执行的方法")
    private String method;
    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private LocalDateTime operateTime;
    /**
     * 耗时
     */
    @Schema(description = "耗时")
    private Long costTime;
    /**
     * 关联模块类型
     */
    @Schema(description = "关联模块类型")
    private String refModule;
    /**
     * 返回结果标识
     */
    // @Schema(description = "返回结果状态码")
    // private Integer resultCode;
    /**
     * 是否成功
     */
    // @Schema(description = "是否成功")
    // private Boolean succeed;
    /**
     * 关联模块id
     */
    @Schema(description = "关联模块id")
    private Long refId;
    /**
     * json格式的返回值
     */
    @Schema(description = "返回值")
    private String result;
    /**
     * json格式的入参
     */
    @Schema(description = "入参")
    private String parameters;
    /**
     * 请求体
     */
    @Schema(description = "请求体")
    private String requestBody;
    /**
     * 请求头
     */
    @Schema(description = "请求头")
    private String headers;
    /**
     * 异常堆栈
     */
    @Schema(description = "异常堆栈")
    private String errorStrace;
}
