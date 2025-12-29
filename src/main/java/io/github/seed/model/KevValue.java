package io.github.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * key-value对象
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "key-value对象")
public abstract class KevValue<T> implements Serializable {

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
