package io.github.seed.model.query;

import io.github.seed.common.constant.Const;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询入参
 *
 * @param <T>
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "分页查询入参")
public class PageQuery<T extends Serializable> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 页数
     */
    @Schema(title = "页数", description = "默认" + Const.PAGE)
    @Min(value = 1, message = "最小页数{value}")
    private int page = Const.PAGE;
    /**
     * 每页条数
     */
    @Schema(title = "每页条数", description = "默认" + Const.PAGE_SIZE)
    @Min(value = 1, message = "每页至少{value}条")
    @Max(value = Const.DB_BATCH_SIZE * 10, message = "每页最多{value}条")
    private int size = Const.PAGE_SIZE;
    /**
     * 排序
     */
    @Schema(title = "排序", example = "create_time asc, id asc")
    private String orderBy;
    /**
     * 总数，用于手动传入总数，不必再查询数量。小于0则表示需要计算总数
     */
    @Schema(title = "总数", description = "有传入大于等于0总数，表示不必再查询数量，小于0则表示需要计算总数")
    private long total = -1;
    /**
     * 查询参数
     */
    @Schema(title = "查询参数")
    @Valid
    private T params;

    public PageQuery(int page, int size, T params) {
        this.page = page;
        this.size = size;
        this.params = params;
    }

}
