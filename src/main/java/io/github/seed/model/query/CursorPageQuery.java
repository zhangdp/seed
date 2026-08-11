package io.github.seed.model.query;

import io.github.seed.common.constant.Const;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 游标分页查询
 *
 * @param <T>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "游标分页查询")
public class CursorPageQuery<T extends Serializable> implements Serializable {

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
     * 是否降序，默认false
     */
    @Schema(title = "是否降序", description = "默认false")
    private boolean desc = false;
    /**
     * 是否计算总数，默认false
     */
    @Schema(title = "是否计算总数", description = "默认false")
    private boolean countTotal = false;
    /**
     * 当前游标
     */
    @Schema(title = "当前游标", description = "空则从头开始")
    private Long cursor;
    /**
     * 查询参数
     */
    @Schema(title = "查询参数")
    @Valid
    private T params;

    public CursorPageQuery(int page, int size, Long cursor, T params) {
        this.page = page;
        this.size = size;
        this.cursor = cursor;
        this.params = params;
    }
}
