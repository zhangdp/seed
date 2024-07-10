package com.zhangdp.seed.model.params;

import com.zhangdp.seed.common.constant.Const;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询基类
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页查询入参")
public class BasePageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页数
     */
    @Schema(title = "页数", description = "默认" + Const.PAGE)
    @Min(value = 1, message = "最小页数{value}")
    protected Integer page = Const.PAGE;
    /**
     * 每页条数
     */
    @Schema(title = "每页条数", description = "默认" + Const.PAGE_SIZE)
    @Min(value = 1, message = "每页至少{value}条")
    @Max(value = 100, message = "每页最多{value}条")
    protected Integer size = Const.PAGE_SIZE;
    /**
     * 排序
     * todo 可能会有sql注入风险
     */
    @Schema(title = "排序", example = "create_time asc, id asc")
    protected String orderBy;

}
