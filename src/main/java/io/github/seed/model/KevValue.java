package io.github.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * key-value对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "key-value对象")
public class KevValue<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 属性
     */
    @Schema(title = "属性")
    protected String key;
    /**
     * 值
     */
    @Schema(title = "值")
    protected T value;
}
