package com.zhangdp.seed.model.query;

import com.zhangdp.seed.common.constant.CommonConst;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(title = "页数", description = "默认" + CommonConst.PAGE)
    @Min(value = 1, message = "最小页数1")
    protected Integer page = CommonConst.PAGE;
    /**
     * 每页条数
     */
    @Schema(title = "每页条数", description = "默认" + CommonConst.PAGE_SIZE)
    @Min(value = 1, message = "至少1条")
    protected Integer size = CommonConst.PAGE_SIZE;
    /**
     * 排序
     */
    @Schema(title = "排序", example = "create_time asc, id asc")
    protected String orderBy;

}
