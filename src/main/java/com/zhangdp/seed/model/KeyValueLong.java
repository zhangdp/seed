package com.zhangdp.seed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 值为Long类型key-value
 *
 * @author zhangdp
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@Schema(description = "key-value(Long类型)对象")
public class KeyValueLong extends KevValue<Long> implements Serializable {

    public KeyValueLong(String key, Long value) {
        super(key, value);
    }
}
