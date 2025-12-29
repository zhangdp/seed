package io.github.seed.model.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2024/10/8
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OperationLogQuery extends BaseQueryParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 操作人
     */
    @Schema(title = "操作人id")
    private Long userId;
    /**
     * 起始时间
     */
    @Schema(title = "起始时间", description = "格式yyyy-MM-dd HH:mm:ss")
    private String startTime;
    /**
     * 结束时间
     */
    @Schema(title = "结束时间", description = "格式yyyy-MM-dd HH:mm:ss")
    private String endTime;
    /**
     * 操作模块
     */
    @Schema(title = "操作模块")
    private String refModule;
    /**
     * 操作方式
     */
    @Schema(title = "操作方式")
    private String type;
    /**
     * 调用的接口url地址
     */
    @Schema(title = "调用的url地址")
    private String uri;

}
