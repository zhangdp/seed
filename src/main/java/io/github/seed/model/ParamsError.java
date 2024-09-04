package io.github.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/5/18 参数校验错误
 *
 * @author zhangdp
 * @since
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "参数校验错误")
public class ParamsError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 字段
     */
    @Schema(title = "字段")
    private String field;
    /**
     * 错误描述
     */
    @Schema(title = "错误描述")
    private String message;
}
