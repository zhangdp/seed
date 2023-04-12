package com.zhangdp.seed.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 使用封装对象的分页查询
 *
 * @param <T>
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "分页查询入参")
public class PageQuery<T extends Serializable> extends BasePageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 查询参数
     */
    @Schema(title = "查询参数")
    @Valid
    private T params;

}
