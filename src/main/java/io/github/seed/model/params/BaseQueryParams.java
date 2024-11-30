package io.github.seed.model.params;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 2023/6/21 基本查询对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "查询入参")
public class BaseQueryParams implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 查询内容
     */
    @Schema(title = "查询内容")
    protected String query;
}
